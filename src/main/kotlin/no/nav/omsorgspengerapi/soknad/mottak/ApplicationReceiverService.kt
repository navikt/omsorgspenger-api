package no.nav.omsorgspengerapi.soknad.mottak

import brave.Tracer
import no.nav.omsorgspengerapi.config.general.webClient.WebClientConfig
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import no.nav.omsorgspengerapi.soker.api.ApplicantLookupException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono

@Service
class ApplicationReceiverService(
        @Qualifier("applicationReceiverClient") val client: WebClient,
        private val apiGatewayApiKey: ApiGatewayApiKey,
        private val tracer: Tracer
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationReceiverService::class.java)
    }

    fun sendApplication(completeApplicationDTO: CompleteApplicationDTO): Mono<Void> {
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
                .bodyValue(completeApplicationDTO)
                .exchange()
                .doOnNext {res: ClientResponse ->
                    val statusCode = res.statusCode()
                    if (statusCode.is5xxServerError) {
                        Mono.error<Void>(ApplicantLookupException("Failed to send application"))
                    }
                }
                .flatMap { it.bodyToMono(Void::class.java) }
                .retryWhen(WebClientConfig.retry)
    }
}