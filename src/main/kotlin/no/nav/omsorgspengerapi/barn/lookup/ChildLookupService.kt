package no.nav.omsorgspengerapi.barn.lookup

import brave.Tracer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Service
class ChildLookupService(private val client: WebClient, private val tracer: Tracer) {

    @Value("\${nav.no.gateways.k9_lookup_url}")
    lateinit var baseUrl: String

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChildLookupService::class.java)
        private val attributter = Pair("a", listOf("barn[].aktør_id",
                "barn[].fornavn",
                "barn[].mellomnavn",
                "barn[].etternavn",
                "barn[].fødselsdato")
        )
    }

    fun lookupChild(): Flux<ChildLookupDTO> {

        return client
                .get()
                .uri(baseUrl)
                .attribute("barn[].aktør_id", "")
                .attribute("barn[].fornavn", "")
                .attribute("barn[].mellomnavn", "")
                .attribute("barn[].etternavn", "")
                .attribute("barn[].fødselsdato", "")
                .header("X-Correlation-ID", tracer.currentSpan().context().traceIdString())
                .retrieve()
                .bodyToFlux(ChildLookupDTO::class.java)
    }

    //fun toChildV1() =
}