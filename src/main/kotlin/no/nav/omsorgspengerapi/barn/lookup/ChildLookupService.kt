package no.nav.omsorgspengerapi.barn.lookup

import brave.Tracer
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux

@Service
class ChildLookupService(
        @Qualifier("k9LookuoClient") private val client: WebClient,
        private val apiGatewayApiKey: ApiGatewayApiKey,
        private val tracer: Tracer) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ChildLookupService::class.java)
        private val attributes = listOf(
                "barn[].aktør_id",
                "barn[].fornavn",
                "barn[].mellomnavn",
                "barn[].etternavn",
                "barn[].fødselsdato")
    }

    fun lookupChildren(): Flux<ChildLookupDTO> {
        return client
                .get()
                .uri { uri: UriBuilder ->
                    uri
                            .path("/meg")
                            .queryParam("a", attributes)
                            .build()
                }
                .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
                .header(apiGatewayApiKey.header, apiGatewayApiKey.key)
                .retrieve()
                .bodyToMono(ChildLookupResponse::class.java)
                .flatMapMany { Flux.fromIterable<ChildLookupDTO>(it.children) }
    }
}