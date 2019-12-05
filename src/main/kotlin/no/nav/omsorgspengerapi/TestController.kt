package no.nav.omsorgspengerapi

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
class TestController(private val webClient: WebClient) {

    @GetMapping("/unprotected")
    fun unprotected(): Mono<ResponseEntity<Void>> {
        return webClient.get()
                .uri("https://www.vg.no")
                .retrieve()
                .toBodilessEntity()

    }
}
