package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.vedlegg.exception.AttachmentContentTypeNotSupported
import no.nav.omsorgspengerapi.vedlegg.exception.AttachmentNotFoundException
import no.nav.omsorgspengerapi.vedlegg.upload.AttachmentId
import no.nav.omsorgspengerapi.vedlegg.upload.K9DocumentService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AttachmentService(private val k9DocumentService: K9DocumentService) {
    private val supportedContentTypes = listOf("application/pdf", "image/jpeg", "image/png")

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentService::class.java)
    }

    fun saveAttachment(attachment: AttachmentFile): Mono<AttachmentId> {
        return if (!attachment.isSupportedContentType()) {
            Mono.error<AttachmentId>(AttachmentContentTypeNotSupported("Attachment with type '${attachment.contentType} ' is not supported. Valid types: $supportedContentTypes"))
        } else {
            k9DocumentService.upladAttachment(attachment)
        }
    }

    fun getAttachmentJson(attachmentId: String): Mono<AttachmentJson> {
        return k9DocumentService.getAttachmentAsJson(attachmentId)
                .onErrorMap { (AttachmentNotFoundException("Attachment with id $attachmentId was not found")) }
    }

    private fun AttachmentFile.isSupportedContentType(): Boolean = supportedContentTypes.contains(contentType)
}