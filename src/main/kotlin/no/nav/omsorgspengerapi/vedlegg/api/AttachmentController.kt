package no.nav.omsorgspengerapi.vedlegg.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@Validated
class AttachmentController(private val attachmentService: AttachmentService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentController::class.java)
    }

    @PostMapping("/vedlegg", consumes = ["multipart/form-data"])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadAttachment(@Valid @RequestPart("file") filePart: FilePart): Mono<AttachmentId>? {
        log.info("File uploaded")

        val attachment = AttachmentFile(
                content = filePart.content(),
                contentType = filePart.headers().contentType.toString(),
                title = filePart.filename()
        )
        log.info("Got attachment: {}", attachment)

        return attachmentService.saveAttachment(attachment)
                .map { attachmentId: AttachmentId ->
                    log.info("Got attachmentId: {}", attachmentId)
                    attachmentId
                }
                .onErrorMap { error: Throwable ->
                    log.error("Failed to upload attachment.", error.cause)
                    error
                }
    }

    @GetMapping("/vedlegg/{vedleggId}")
    fun getAttachment(@PathVariable("vedleggId") attachmentId: String): Mono<AttachmentJson> {
        log.info("Fetching attachment with id: {}", attachmentId)
        return attachmentService.getAttachmentJson(attachmentId = attachmentId)
    }

    @DeleteMapping("/vedlegg/{vedleggId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAttachment(@PathVariable("vedleggId") attachmentId: String): Mono<Void> {
        log.info("Deleting attachment with id: {}", attachmentId)
        return attachmentService.deleteAttachment(attachmentId)
    }
}