package no.nav.omsorgspengerapi.soker.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.helse.soker.ApplicantV1
import no.nav.omsorgspengerapi.docs.SELVBETJENING_ID_TOKEN_SCHEME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@SecurityRequirement(name = SELVBETJENING_ID_TOKEN_SCHEME)
@Tag(name = "Søker", description = "Endepunkt for innhenting av informasjon om søker (innlogget bruker)")
class ApplicantController(private val applicantService: ApplicantService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicantController::class.java)
    }

    @GetMapping("/soker")
    fun getSoker(): Mono<ApplicantV1> {
        return applicantService.getApplicant()
    }
}