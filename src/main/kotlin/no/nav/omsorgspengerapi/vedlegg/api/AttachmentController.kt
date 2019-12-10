package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.vedlegg.upload.AttachmentId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
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
    fun uploadAttachment(@Valid @RequestPart("file") filePart: FilePart, webExchange: ServerWebExchange): Mono<AttachmentId>? {
        log.info("File uploaded")

        val attachment = Attachment(
                content = filePart.content(),
                contentType = filePart.headers().contentType!!,
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
}