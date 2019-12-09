package no.nav.omsorgspengerapi.soker.lookup

import brave.Tracer
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono

@Service
class ApplicantLookupService(
        @Qualifier("k9LookuoClient") private val client: WebClient,
        private val apiGatewayApiKey: ApiGatewayApiKey,
        private val tracer: Tracer
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicantLookupService::class.java)
        private val attributes = listOf(
                "aktør_id",
                "fornavn",
                "mellomnavn",
                "etternavn",
                "fødselsdato")
    }

    fun lookupApplicant(): Mono<ApplicanLookupDTO> {
        return client
                .get()
                .uri { uri: UriBuilder ->
                    uri
                            .path("/meg")
                            .queryParam("a", attributes)
                            .build()
                }
                .header("X-Correlation-ID", tracer.currentSpan().context().traceIdString())
                .header(apiGatewayApiKey.header, apiGatewayApiKey.key)
                .retrieve()
                .bodyToMono(ApplicanLookupDTO::class.java)
    }
}