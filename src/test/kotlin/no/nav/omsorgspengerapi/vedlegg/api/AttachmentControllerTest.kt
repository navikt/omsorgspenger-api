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
@WebFluxTest(AttachmentController::class)
@WithMockUser()
@ActiveProfiles("test")
class AttachmentControllerTest {

    @MockkBean
    lateinit var attachmentService: AttachmentService

    @Autowired
    lateinit var client: WebTestClient

    @Test
    fun `when uploading an attachment, expect attachment id`() {

        val filePart = ClassPathResource("./files/spring-kotlin-59kb.png")
                .toMultipartBody(MimeTypeUtils.IMAGE_PNG)

        every { attachmentService.saveAttachment(capture(slot())) } returns Mono.just(AttachmentId(UUID.randomUUID().toString()))

        client.mutateWith(csrf())
                .post()
                .uri("/vedlegg")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().isCreated
    }

    @Test
    fun `when uploading an unsupported attachment, expect bad request`() {

        val filePart = ClassPathResource("./files/unsupported-file.txt")
                .toMultipartBody(MimeTypeUtils.TEXT_PLAIN)

        val errorMessage = "Attachment with type '${MimeTypeUtils.TEXT_PLAIN_VALUE} ' is not supported."

        every { attachmentService.saveAttachment(capture(slot())) } returns Mono.error<AttachmentId>(DocumentContentTypeNotSupported(errorMessage))

        val actualError = client.mutateWith(csrf())
                .post()
                .uri("/vedlegg")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(DocumentContentTypeNotSupported::class.java)
                .returnResult().responseBody

        assertThat(actualError)
                .hasMessage(errorMessage)
    }

    @Test
    fun `when uploading a big attachment, expect bad request`() {

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
    fun `when getting an attachment, expect an attachment with json response`() {
        val file = ClassPathResource("./files/spring-kotlin-59kb.png").file
        val expectedAttachment = AttachmentJson(
                content = file.readBytes(),
                contentType = MimeTypeUtils.IMAGE_PNG_VALUE,
                title = file.nameWithoutExtension
        )

        every { attachmentService.getAttachmentJson(capture(slot())) } returns Mono.just(expectedAttachment)

        val actualAttachment = client.get()
                .uri("/vedlegg/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(AttachmentJson::class.java)
                .returnResult().responseBody

        assertThat(actualAttachment).isEqualTo(expectedAttachment)
    }

    @Test
    fun `when attachment is not found, expect not found`() {

        val errorMessage = "Attachment with id 1 was not found"

        every { attachmentService.getAttachmentJson(capture(slot())) } returns Mono.error(DocumentNotFoundException(errorMessage))

        val actualError = client.get()
                .uri("/vedlegg/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(DocumentNotFoundException::class.java)
                .returnResult().responseBody

        assertThat(actualError).hasMessage(errorMessage)
    }

    @Test
    fun `when attachment retrieval fails, expect internal server error`() {

        val errorMessage = "Failed to retrieve attachment, due to upstream issues"

        every { attachmentService.getAttachmentJson(capture(slot())) } returns Mono.error(DocumentRetrievalFailedException(errorMessage))

        val actualError = client.get()
                .uri("/vedlegg/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(DocumentRetrievalFailedException::class.java)
                .returnResult().responseBody

        assertThat(actualError).hasMessage(errorMessage)
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
}