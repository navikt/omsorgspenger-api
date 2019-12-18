package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Mono
import java.util.*

@ExtendWith(MockitoExtension::class)
@WebFluxTest(AttachmentController::class)
@WithMockUser()
@ActiveProfiles("test")
class AttachmentControllerTest {

    @MockBean
    lateinit var attachmentService: AttachmentService

    @Autowired
    lateinit var client: WebTestClient

    @Test
    fun `when uploading an attachment, expect attachment id`() {

        val filePart = ClassPathResource("./images/spring-kotlin-59kb.png")
                .toMultipartBody(MimeTypeUtils.IMAGE_PNG)

        `when`(attachmentService.saveAttachment(any(AttachmentFile::class.java)))
                .thenReturn(Mono.just(AttachmentId(UUID.randomUUID().toString())))

        client.mutateWith(csrf())
                .post()
                .uri("/vedlegg")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().isCreated
    }

    @Test
    fun `when uploading a big attachment, expect bad request`() {

        val filePart = ClassPathResource("./images/554kb.jpeg")
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

    private fun ClassPathResource.toMultipartBody(fileType: MimeType): MultiValueMap<String, HttpEntity<*>> {
        val bodyBuilder = MultipartBodyBuilder()
        bodyBuilder
                .part("file", this.file.readBytes())
                .filename(this.file.name)
                .header("Content-Disposition", "form-data; filename=${this.file.name}")
                .header("Content-Type", fileType.toString())

        return bodyBuilder.build()
    }

    // TODO: Move to common testUtil for re-usability
    private fun <T> any(type: Class<T>): T {
        Mockito.any(type)
        return null as T
    }
}