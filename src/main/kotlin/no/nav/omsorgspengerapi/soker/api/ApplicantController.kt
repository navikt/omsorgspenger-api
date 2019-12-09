package no.nav.omsorgspengerapi.soker.api

import no.nav.helse.soker.ApplicantV1
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ApplicantController(private val applicantService: ApplicantService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicantController::class.java)
    }

    @GetMapping("/soker")
    fun getSoker(): Mono<ApplicantV1> {
        return applicantService.getApplicant()
    }
}