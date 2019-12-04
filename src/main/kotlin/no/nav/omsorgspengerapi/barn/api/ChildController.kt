package no.nav.omsorgspengerapi.barn.api

import no.nav.omsorgspengerapi.barn.lookup.ChildLookupDTO
import no.nav.omsorgspengerapi.barn.lookup.ChildLookupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.LocalDate

@RestController
class ChildController(private val childLookupService: ChildLookupService) {

    @GetMapping("/barn")
    fun getChild(): Flux<ChildLookupDTO> {
        return childLookupService.lookupChild()
    }

    @GetMapping("/child-lookup/meg")
    fun mockChildLookup(): List<ChildLookupDTO> {
        return listOf<ChildLookupDTO>(
                ChildLookupDTO(LocalDate.now().minusYears(20), "Mock", "Mocki", "Mockesen", "123456")
        )
    }
}