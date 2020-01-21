package no.nav.omsorgspengerapi.soker.lookup

import brave.Tracer
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import no.nav.omsorgspengerapi.soker.api.SøkerOppslagException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono

@Service
class SøkerOppslagsService(
        @Qualifier("k9LookuoClient") private val client: WebClient,
        private val apiGatewayApiKey: ApiGatewayApiKey,
        private val tracer: Tracer
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(SøkerOppslagsService::class.java)
        private val attributter = listOf(
                "aktør_id",
                "fornavn",
                "mellomnavn",
                "etternavn",
                "fødselsdato")
    }

    fun slåOppSøker(): Mono<SøkerOppslagsDTO> {
        return client
                .get()
                .uri { uri: UriBuilder ->
                    uri
                            .path("/meg")
                            .queryParam("a", attributter)
                            .build()
                }
                .header(NavHeaders.XCorrelationId, tracer.currentSpan().context().traceIdString())
                .header(apiGatewayApiKey.header, apiGatewayApiKey.key)
                .retrieve()
                .bodyToMono(SøkerOppslagsDTO::class.java)
                //.retryWhen(WebClientConfig.retry)
                .onErrorMap { SøkerOppslagException("Oppslag av søker feilet.") }
    }
}