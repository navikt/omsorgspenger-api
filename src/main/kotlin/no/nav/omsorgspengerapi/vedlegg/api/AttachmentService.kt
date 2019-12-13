package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.vedlegg.exception.DocumentContentTypeNotSupported
import no.nav.omsorgspengerapi.vedlegg.document.DocumentFile
import no.nav.omsorgspengerapi.vedlegg.document.K9DocumentService
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
            Mono.error<AttachmentId>(DocumentContentTypeNotSupported("Attachment with type '${attachment.contentType} ' is not supported. Valid types: $supportedContentTypes"))
        } else {
            k9DocumentService.uploadDocument(DocumentFile(
                    title = attachment.title,
                    contentType = attachment.contentType,
                    content = attachment.content
            ))
                    .map { AttachmentId(id = it.id) }
        }
    }

    fun getAttachmentJson(attachmentId: String): Mono<AttachmentJson> {
        return k9DocumentService.getDocumentAsJson(attachmentId)
                .map {
                    AttachmentJson(
                            title = it.title,
                            contentType = it.contentType,
                            content = it.content
                    )
                }
    }

    private fun AttachmentFile.isSupportedContentType(): Boolean = supportedContentTypes.contains(contentType)
}