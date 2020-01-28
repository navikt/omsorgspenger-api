package no.nav.omsorgspengerapi.soknad.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.omsorgspengerapi.common.IdToken
import no.nav.omsorgspengerapi.docs.SELVBETJENING_ID_TOKEN_SCHEME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@SecurityRequirement(name = SELVBETJENING_ID_TOKEN_SCHEME)
@Tag(name = "Søknad", description = "Endepunkt for innsending av søknad om omsorgspenger.")
class SøknadController(private val søknadService: SøknadService) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(SøknadController::class.java)
    }

    @PostMapping(value = ["/soknad"], consumes = [APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun mottaSøknad(@RequestBody søknad: Søknad, @RequestHeader(AUTHORIZATION) authToken: String): Mono<SøknadId> {
        val idToken = IdToken(authToken)
        log.info("Innsendt søknad: {}", søknad)

        log.info("Validerer søknad...")
        søknad.valider()
        log.info("Søknad validert")

        return when {
            idToken.getSubject().isNullOrBlank() -> Mono.error(IllegalStateException("Ugyldig id token. Token mangler 'sub' claim."))
            else -> søknadService.sendSoknad(søknad, idToken)
        }
    }
}