package no.nav.omsorgspengerapi.barn.lookup

import brave.Tracer
import no.nav.omsorgspengerapi.barn.api.BarnOppslagFeiletException
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BarnOppslagsService(
        @Qualifier("k9LookuoClient") private val client: WebClient,
        private val apiGatewayApiKey: ApiGatewayApiKey,
        private val tracer: Tracer) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(BarnOppslagsService::class.java)
        private val attributter = listOf(
                "barn[].aktør_id",
                "barn[].fornavn",
                "barn[].mellomnavn",
                "barn[].etternavn",
                "barn[].fødselsdato")
    }

    fun slåOppBarn(): Flux<BarnOppslagDTO> = client
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
            .onStatus(HttpStatus::isError) { clientResponse: ClientResponse ->
                Mono.error(BarnOppslagFeiletException("Feilet ved oppslag av barn."))
            }
            .bodyToMono(BarnOppslagRespons::class.java)
            .flatMapIterable { it.barn }
    //.retryWhen(WebClientConfig.retry)
}
