package no.nav.omsorgspengerapi.barn.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.omsorgspengerapi.docs.SELVBETJENING_ID_TOKEN_SCHEME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@SecurityRequirement(name = SELVBETJENING_ID_TOKEN_SCHEME)
@Tag(name = "Barn", description = "Endepunkt for innhenting av informasjon om søkers barn (innlogget bruker)")
class BarnController(private val barnService: BarnService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(BarnController::class.java)
    }

    @GetMapping("/barn")
    fun getBarn(): Flux<Barn> {
        return barnService.getBarn()
    }
}