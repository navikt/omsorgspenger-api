package no.nav.omsorgspengerapi.vedlegg.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Mono
import java.util.*

@ExtendWith(SpringExtension::class)
@WebFluxTest(VedleggController::class)
@WithMockUser()
@ActiveProfiles("test")
class VedleggControllerTest {

    @MockkBean
    lateinit var vedleggService: VedleggService

    @Autowired
    lateinit var client: WebTestClient

    @Test
    fun `Ved opplasting av vedlegg, forvent vedleggid`() {

        val filePart = ClassPathResource("./files/spring-kotlin-59kb.png")
                .toMultipartBody(MimeTypeUtils.IMAGE_PNG)

        every { vedleggService.lagreVedlegg(capture(slot())) } returns Mono.just(VedleggId(UUID.randomUUID().toString()))

        client.mutateWith(csrf())
                .post()
                .uri("/vedlegg")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().isCreated
    }

    @Test
    fun `Ved opplasting av ugylid vedleggstype, forvent status BAD_REQUEST`() {

        val filePart = ClassPathResource("./files/unsupported-file.txt")
                .toMultipartBody(MimeTypeUtils.TEXT_PLAIN)

        val errorMessage = "Vedlegg med type '${MimeTypeUtils.TEXT_PLAIN_VALUE} ' er ikke supportert"

        every { vedleggService.lagreVedlegg(capture(slot())) } returns Mono.error<VedleggId>(VedleggtypeIkkeSupportertException(errorMessage))

        val actualError = client.mutateWith(csrf())
                .post()
                .uri("/vedlegg")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(VedleggtypeIkkeSupportertException::class.java)
                .returnResult().responseBody

        assertThat(actualError)
                .hasMessage(errorMessage)
    }

    @Test
    fun `Ved opplasting av for stor vedlegg, forvent status BAD_REQUEST`() {

        val filePart = ClassPathResource("./files/554kb.jpeg")
                .toMultipartBody(MimeTypeUtils.IMAGE_JPEG)

        val expectedError = OmsorgspengerAPIError(
                error = "org.springframework.core.codec.DecodingException",
                message = "Failure while parsing part[1]; nested exception is org.springframework.core.io.buffer.DataBufferLimitException: Part[1] exceeded the in-memory limit of 200000 bytes",
                violations = mutableSetOf(),
                path = "/vedlegg",
                status = 400
        )

        val actualError = client.mutateWith(csrf())
                .post()
                .uri("/vedlegg")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(OmsorgspengerAPIError::class.java)
                .returnResult().responseBody

        assertThat(actualError).isEqualToIgnoringGivenFields(expectedError, "timestamp")
    }

    @Test
    fun `Ved henting av vedlegg, forvent vedleggJson`() {
        val file = ClassPathResource("./files/spring-kotlin-59kb.png").file
        val forventetVedlegg = VedleggJson(
                content = file.readBytes(),
                contentType = MimeTypeUtils.IMAGE_PNG_VALUE,
                title = file.nameWithoutExtension
        )

        every { vedleggService.hentVedleggSomJson(capture(slot())) } returns Mono.just(forventetVedlegg)

        val vedlegg = client.get()
                .uri("/vedlegg/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(VedleggJson::class.java)
                .returnResult().responseBody

        assertThat(vedlegg).isEqualTo(forventetVedlegg)
    }

    @Test
    fun `Når vedlegg ikke blir funnet, forvent status NOT_FOUND`() {

        val errorMessage = "Attachment with id 1 was not found"

        every { vedleggService.hentVedleggSomJson(capture(slot())) } returns Mono.error(VedleggIkkeFunnetException(errorMessage))

        val actualError = client.get()
                .uri("/vedlegg/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(VedleggIkkeFunnetException::class.java)
                .returnResult().responseBody

        assertThat(actualError).hasMessage(errorMessage)
    }

    @Test
    fun `Når henting av vedlegg feiler, forvent status INTERNAL_SERVER_ERROR`() {

        val forventetFeilmelding = "Henting av vedlegg feilet"

        every { vedleggService.hentVedleggSomJson(capture(slot())) } returns Mono.error(VedleggHentingFeiletException(forventetFeilmelding))

        val feilmelding = client.get()
                .uri("/vedlegg/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(VedleggHentingFeiletException::class.java)
                .returnResult().responseBody

        assertThat(feilmelding).hasMessage(forventetFeilmelding)
    }

    private fun ClassPathResource.toMultipartBody(fileType: MimeType): MultiValueMap<String, HttpEntity<*>> {
        val bodyBuilder = MultipartBodyBuilder()
        bodyBuilder
                .part("fil", this.file.readBytes())
                .filename(this.file.name)
                .header("Content-Disposition", "form-data; filename=${this.file.name}")
                .header("Content-Type", fileType.toString())

        return bodyBuilder.build()
    }
}