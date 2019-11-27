package no.nav.omsorgspengerapi

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TestController {

    @GetMapping("/unprotected")
    fun unprotected(): Mono<String> {
        return Mono.just("UnProtected")
    }
}
