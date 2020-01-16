package no.nav.omsorgspengerapi.soknad.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.omsorgspengerapi.docs.SELVBETJENING_ID_TOKEN_SCHEME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@SecurityRequirement(name = SELVBETJENING_ID_TOKEN_SCHEME)
@Tag(name = "Søknad", description = "Endepunkt for innsending av søknad om omsorgspenger.")
class ApplicationController(private val applicationService: ApplicationService) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationController::class.java)
    }

    @PostMapping("/soknad")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendApplication(@RequestBody application: ApplicationV1) {
        log.info("Application received: {}", application)

        log.info("Validating application...")
        application.validate()
        log.info("Application validated")
        applicationService.sendSoknad(application)
    }
}