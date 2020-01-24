package no.nav.omsorgspengerapi.soknad.mottak

import brave.Tracer
import no.nav.helse.dusseldorf.oauth2.client.AccessTokenClient
import no.nav.helse.dusseldorf.oauth2.client.CachedAccessTokenClient
import no.nav.omsorgspengerapi.config.general.webClient.WebClientConfig
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import no.nav.omsorgspengerapi.soknad.api.SøknadId
import no.nav.omsorgspengerapi.soknad.api.SøknadInnsendingFeiletException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono

@Service
class SøknadMottakService(
        @Qualifier("mottakTokenAccessClient") private val accessTokenClient: AccessTokenClient,
        @Value("#{'\${nav.no.security.scopes.omsorgspengesoknad-mottak-scopes}'.split(',')}") private val sendeTilMottakScopes: Set<String>,
        @Qualifier("søknadMottaksKlient") val client: WebClient,
        private val apiGatewayApiKey: ApiGatewayApiKey,
        private val tracer: Tracer
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SøknadMottakService::class.java)
    }

    fun sendSøknad(komplettSøknadDTO: KomplettSøknadDTO): Mono<SøknadId> {
        return client
                .post()
                .uri { uri: UriBuilder ->
                    uri
                            .path("/v1")
                            .path("/soknad")
                            .build()
                }
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Correlation-ID", tracer.currentSpan().context().traceIdString())
                .header(apiGatewayApiKey.header, apiGatewayApiKey.key)
                .header(HttpHeaders.AUTHORIZATION, systemToken())
                .bodyValue(komplettSøknadDTO)
                .retrieve()
                .bodyToMono(SøknadId::class.java)
                .retryWhen(WebClientConfig.retry)
                .onErrorMap { SøknadInnsendingFeiletException("Innsending av søknad feilet.") }
    }

    private fun systemToken(): String = CachedAccessTokenClient(accessTokenClient).getAccessToken(sendeTilMottakScopes).asAuthoriationHeader()
}