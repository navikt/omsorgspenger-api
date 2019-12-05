package no.nav.omsorgspengerapi.barn.api

import no.nav.omsorgspengerapi.barn.lookup.ChildLookupDTO
import no.nav.omsorgspengerapi.barn.lookup.ChildLookupResponse
import no.nav.omsorgspengerapi.barn.lookup.ChildLookupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@RestController
class ChildController(private val childLookupService: ChildLookupService) {

    @GetMapping("/barn")
    fun getChild(): Mono<ChildLookupResponse> {
        return childLookupService.lookupChild()
    }

    @GetMapping("/child-lookup/meg")
    fun mockChildLookup(): ChildLookupResponse {
        return ChildLookupResponse(child = listOf(
                ChildLookupDTO(
                        fodselsdato = LocalDate.now().minusYears(20),
                        fornavn = "Mock",
                        mellomnavn = "Mocki",
                        etternavn = "Mockesen",
                        aktoerId = "123456"
                )
        ))
    }
}