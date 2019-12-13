package no.nav.omsorgspengerapi.vedlegg.document

import brave.Tracer
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.general.webClient.WebClientConfig
import no.nav.omsorgspengerapi.vedlegg.exception.DocumentNotFoundException
import no.nav.omsorgspengerapi.vedlegg.exception.DocumentUploadFailedException
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
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono


@Service
class K9DocumentService(
        @Qualifier("k9DocumentClient") private val client: WebClient,
        private val tracer: Tracer
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9DocumentService::class.java)
    }


    fun uploadDocument(document: DocumentFile): Mono<DocumentId> {
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
                .bodyValue(document.toMultiPartBody())
                .exchange()
                .doOnNext {res: ClientResponse ->
                    val statusCode = res.statusCode()
                    if (statusCode.is4xxClientError) {
                        Mono.error<DocumentId>((DocumentUploadFailedException("Failed to upload document")))
                    }
                }
                .flatMap { it.bodyToMono(DocumentId::class.java) }
                .retryWhen(WebClientConfig.retry)
    }

    fun getDocumentAsJson(documentId: String): Mono<DocumentJson> {
        return client
                .get()
                .uri { uri: UriBuilder ->
                    uri
                            .path("/v1")
                            .path("/dokument")
                            .path("/${documentId}")
                            .build()
                }
                .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .doOnNext {res: ClientResponse ->
                    val statusCode = res.statusCode()
                    if (statusCode.value() == 404) {
                        Mono.error<DocumentJson>((DocumentNotFoundException("Document with id $documentId was not found")))
                    }
                }
                .flatMap { it.bodyToMono(DocumentJson::class.java) }
                .retryWhen(WebClientConfig.retry)
    }

    fun deleteDocument(documentId: String): Mono<Void> {
        return client
                .delete()
                .uri { uri: UriBuilder ->
                    uri
                            .path("/v1")
                            .path("/dokument")
                            .path("/${documentId}")
                            .build()
                }
                .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .doOnNext { res: ClientResponse ->
                    val statusCode = res.statusCode()
                    if (statusCode.value() == 404) {
                        Mono.error<DocumentJson>((DocumentNotFoundException("Document with id $documentId was not found")))
                    }
                }
                .flatMap { it.bodyToMono(Void::class.java) }
                .retryWhen(WebClientConfig.retry)
    }

    private fun DocumentFile.toMultiPartBody(): MultiValueMap<String, HttpEntity<*>> {
        val partBuilder = MultipartBodyBuilder()
        partBuilder
                .asyncPart("content", content, DataBuffer::class.java)
                .filename(title)
                .contentType(MediaType.valueOf(contentType))

        partBuilder.part("title", title)
        return partBuilder.build()
    }
}
