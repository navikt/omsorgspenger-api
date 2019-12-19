package no.nav.omsorgspengerapi.soknad.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
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