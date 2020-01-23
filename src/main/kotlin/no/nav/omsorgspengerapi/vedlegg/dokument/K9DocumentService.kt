package no.nav.omsorgspengerapi.vedlegg.dokument

import brave.Tracer
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.general.webClient.WebClientConfig
import no.nav.omsorgspengerapi.vedlegg.api.VedleggHentingFeiletException
import no.nav.omsorgspengerapi.vedlegg.api.VedleggIkkeFunnetException
import no.nav.omsorgspengerapi.vedlegg.api.VedleggOpplastingFeiletException
import no.nav.omsorgspengerapi.vedlegg.api.VedleggSlettingFeiletException
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
        @Qualifier("k9DokumentKlient") private val client: WebClient,
        private val tracer: Tracer
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9DocumentService::class.java)

    }

    fun lastOppDokument(dokument: DokumentFilDTO): Mono<DocumentIdDTO> = client
            .post()
            .uri { uri: UriBuilder ->
                uri
                        .path("/v1")
                        .path("/dokument")
                        .build()
            }
            .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
            .bodyValue(dokument.toMultiPartBody())
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError) { clientResponse: ClientResponse ->
                Mono.error(VedleggOpplastingFeiletException("Opplasting av vedlegg feilet"))
            }
            .bodyToMono(DocumentIdDTO::class.java)
            .retryWhen(WebClientConfig.retry)

    fun hentDokumentSomJson(dokumentId: String): Mono<DocumentJsonDTO> = client
            .get()
            .uri { uri: UriBuilder ->
                uri
                        .path("/v1")
                        .path("/dokument")
                        .path("/${dokumentId}")
                        .build()
            }
            .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .onStatus(
                    { status: HttpStatus -> status == HttpStatus.NOT_FOUND },
                    { clientResponse: ClientResponse -> Mono.error(VedleggIkkeFunnetException("Vedlegg med id $dokumentId ikke funnet")) }
            )
            .onStatus(HttpStatus::is5xxServerError) { clientResponse: ClientResponse ->
                Mono.error(VedleggHentingFeiletException("Henting av vedlegg feilet"))
            }
            .bodyToMono(DocumentJsonDTO::class.java)
            .retryWhen(WebClientConfig.retry)

    fun hentDokumenter(ids: List<String>): Flux<DocumentJsonDTO> = Flux.fromIterable<String>(ids)
            .parallel()
            .runOn(Schedulers.single())
            .flatMap { hentDokumentSomJson(it) }
            .sequential()

    fun slettDokument(dokumentId: String): Mono<Void> = client
            .delete()
            .uri { uri: UriBuilder ->
                uri
                        .path("/v1")
                        .path("/dokument")
                        .path("/${dokumentId}")
                        .build()
            }
            .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .onStatus(
                    { status: HttpStatus -> status == HttpStatus.NOT_FOUND },
                    { clientResponse: ClientResponse -> Mono.error(VedleggIkkeFunnetException("Vedlegg med id $dokumentId ikke funnet")) }
            )
            .onStatus(HttpStatus::is5xxServerError) { clientResponse: ClientResponse ->
                Mono.error(VedleggSlettingFeiletException("Sletting av vedlegg med id $dokumentId feilet"))
            }
            .bodyToMono(Void::class.java)
            .retryWhen(WebClientConfig.retry)

    private fun DokumentFilDTO.toMultiPartBody(): MultiValueMap<String, HttpEntity<*>> {
        val partBuilder = MultipartBodyBuilder()
        partBuilder
                .asyncPart("content", content, DataBuffer::class.java)
                .filename(title)
                .contentType(MediaType.valueOf(contentType))

        partBuilder.part("title", title)
        return partBuilder.build()
    }
}
