package no.nav.omsorgspengerapi.barn.api

import no.nav.omsorgspengerapi.barn.lookup.ChildLookupDTO
import no.nav.omsorgspengerapi.barn.lookup.ChildLookupResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@RestController
class ChildController(private val childService: ChildService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ChildController::class.java)
    }

    @GetMapping("/barn")
    fun getChild(): Flux<ChildV1> {
        return childService.getChildren()
    }
}