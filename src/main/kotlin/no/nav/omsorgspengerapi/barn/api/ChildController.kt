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

    @GetMapping("/child-lookup/meg")
    fun mockChildLookup(): Mono<ChildLookupResponse> {
        return Mono.just(ChildLookupResponse(children = listOf(
                ChildLookupDTO(
                        fodselsdato = LocalDate.now().minusYears(20),
                        fornavn = "Mock",
                        mellomnavn = "Mocki",
                        etternavn = "Mockesen",
                        aktoerId = "123456"
                ),
                ChildLookupDTO(
                        fodselsdato = LocalDate.now().minusYears(20),
                        fornavn = "Mock2",
                        mellomnavn = "Mocki2",
                        etternavn = "Mockesen2",
                        aktoerId = "123457"
                )
        )))
    }
}