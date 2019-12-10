package no.nav.omsorgspengerapi.vedlegg.upload

import brave.Tracer
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.vedlegg.api.Attachment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono


@Service
class AttachmentUploadService(
        @Qualifier("k9DocumentClient") private val client: WebClient,
        private val tracer: Tracer
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentUploadService::class.java)
    }

    fun upladAttachment(attachment: Attachment): Mono<AttachmentId> {
        return client
                .post()
                .uri { uri: UriBuilder ->
                    uri
                            .path("/v1")
                            .path("/dokument")
                            .build()
                }
                .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .bodyValue(attachment.toMultiPartBody())
                .retrieve()
                .bodyToMono(AttachmentId::class.java)
    }

    private fun Attachment.toMultiPartBody(): MultiValueMap<String, HttpEntity<*>> {
        val partBuilder = MultipartBodyBuilder()
        partBuilder
                .asyncPart("content", content, DataBuffer::class.java)
                .filename(title)
                .contentType(contentType)

        partBuilder.part("title", title)
        return partBuilder.build()
    }
}