package no.nav.omsorgspengerapi.soker

import brave.Tracer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
class SokerController(private val client: WebClient, private val tracer: Tracer) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SokerController.javaClass)
    }

    @GetMapping("/soker")
    fun getSoker(): Mono<String> {

        return client
                .get()
                .uri("http://localhost:8080/unprotected")
                .header("X-Correlation-ID\"", tracer.currentSpan().context().traceIdString())
                .retrieve()
                .bodyToMono(String::class.java)
    }
}