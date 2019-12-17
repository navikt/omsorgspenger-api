package no.nav.omsorgspengerapi.vedlegg.document

import brave.Tracer
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.general.webClient.WebClientConfig
import no.nav.omsorgspengerapi.vedlegg.api.DocumentDeletionFailedException
import no.nav.omsorgspengerapi.vedlegg.api.DocumentNotFoundException
import no.nav.omsorgspengerapi.vedlegg.api.DocumentUploadFailedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers


@Service
class K9DocumentService(
        @Qualifier("k9DocumentClient") private val client: WebClient,
        private val tracer: Tracer
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9DocumentService::class.java)

    }



    fun uploadDocument(document: DocumentFile): Mono<DocumentId> = client
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
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError) { clientResponse: ClientResponse ->
                Mono.error(DocumentUploadFailedException("Failed to upload document to upstream service"))
            }
            .bodyToMono(DocumentId::class.java)
            .retryWhen(WebClientConfig.retry)

    fun getDocumentAsJson(documentId: String): Mono<DocumentJson> = client
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
            .retrieve()
            .onStatus(
                    { status: HttpStatus -> status == HttpStatus.NOT_FOUND },
                    { clientResponse: ClientResponse -> Mono.error(DocumentNotFoundException("Attachment with id $documentId was not found")) }
            )
            .onStatus(HttpStatus::is5xxServerError) { clientResponse: ClientResponse ->
                Mono.error(DocumentUploadFailedException("Failed to upload document to upstream service"))
            }
            .bodyToMono(DocumentJson::class.java)
            .retryWhen(WebClientConfig.retry)

    fun getDocuments(ids: List<String>): Flux<DocumentJson> = Flux.fromIterable<String>(ids)
            .parallel()
            .runOn(Schedulers.elastic())
            .flatMap { getDocumentAsJson(it) }
            .sequential()

    fun deleteDocument(documentId: String): Mono<Void> = client
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
            .retrieve()
            .onStatus(
                    { status: HttpStatus -> status == HttpStatus.NOT_FOUND },
                    { clientResponse: ClientResponse -> Mono.error(DocumentNotFoundException("Attachment with id $documentId was not found in upstream service")) }
            )
            .onStatus(HttpStatus::is5xxServerError) { clientResponse: ClientResponse ->
                Mono.error(DocumentDeletionFailedException("Failed to delete attachment with id $documentId from upstream service"))
            }
            .bodyToMono(Void::class.java)
            .retryWhen(WebClientConfig.retry)

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
