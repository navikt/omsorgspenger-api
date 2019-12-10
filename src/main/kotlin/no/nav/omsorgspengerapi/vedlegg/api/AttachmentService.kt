package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.vedlegg.upload.AttachmentId
import no.nav.omsorgspengerapi.vedlegg.upload.AttachmentUploadService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AttachmentService(private val attachmentUploadService: AttachmentUploadService) {
    private val supportedContentTypes = listOf("application/pdf", "image/jpeg", "image/png")

    fun saveAttachment(attachment: Attachment): Mono<AttachmentId> {
        return attachmentUploadService.upladAttachment(attachment)
    }

    private fun Attachment.isSupportedContentType(): Boolean = supportedContentTypes.contains(contentType.toString())
}