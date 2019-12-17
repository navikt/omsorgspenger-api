package no.nav.omsorgspengerapi.soknad.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ApplicationController(private val applicationService: ApplicationService) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationController::class.java)
    }

    @PostMapping("/soknad")
    fun sendApplication(@RequestBody application: ApplicationV1): Mono<Unit> {
        log.info("Application received: {}", application)

        log.info("Validating application...")
        application.validate()
        log.info("Application validated")
        return applicationService.sendSoknad(application)
    }
}