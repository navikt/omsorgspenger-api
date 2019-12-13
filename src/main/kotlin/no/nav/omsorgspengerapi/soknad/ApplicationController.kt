package no.nav.omsorgspengerapi.soknad

import no.nav.omsorgspengerapi.soknad.api.ApplicationV1
import no.nav.omsorgspengerapi.soknad.api.validate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicationController {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationController::class.java)
    }

    @PostMapping("/soknad")
    fun sendApplication(@RequestBody application: ApplicationV1) {
        log.info("Application received: {}", application)
        log.info("Validating application")
        application.validate()
    }
}