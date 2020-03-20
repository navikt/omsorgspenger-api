package no.nav.omsorgspenger.soknad

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import no.nav.omsorgspenger.general.auth.IdTokenProvider
import no.nav.omsorgspenger.general.getCallId
import no.nav.omsorgspenger.soknadOverforeDager.SøknadOverføreDager
import no.nav.omsorgspenger.soknadOverforeDager.SøknadOverføreDagerService
import no.nav.omsorgspenger.soknadOverforeDager.valider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("nav.soknadApis")

@KtorExperimentalLocationsAPI
fun Route.søknadApis(
    søknadService: SøknadService,
    søknadOverføreDagerService: SøknadOverføreDagerService,
    idTokenProvider: IdTokenProvider
) {

    @Location("/soknad")
    class sendSoknad

    post { _ : sendSoknad ->
        logger.trace("Mottatt ny søknad. Mapper søknad.")
        val søknad = call.receive<Søknad>()
        logger.trace("Søknad mappet. Validerer")

        søknad.valider()
        logger.trace("Validering OK. Registrerer søknad.")

        søknadService.registrer(
            søknad = søknad,
            callId = call.getCallId(),
            idToken = idTokenProvider.getIdToken(call)
        )

        logger.trace("Søknad registrert.")
        call.respond(HttpStatusCode.Accepted)
    }

    @Location("/soknad/overfore-omsorgsdager")
    class sendSoknadOverforeDager

    post { _ : sendSoknadOverforeDager ->
        logger.trace("Mottatt ny søknad for overføring av dager. Mapper søknad.")
        val søknadOverføreDager = call.receive<SøknadOverføreDager>()
        logger.trace("Søknad mappet. Validerer")

        søknadOverføreDager.valider()
        logger.trace("Validering OK. Registrerer søknad.")

        søknadOverføreDagerService.registrer(
            søknadOverføreDager = søknadOverføreDager,
            callId = call.getCallId(),
            idToken = idTokenProvider.getIdToken(call)
        )

        logger.trace("Søknad registrert.")
        call.respond(HttpStatusCode.Accepted)
    }

    @Location("/soknad/valider")
    class validerSoknad

    post { _: validerSoknad ->
        val søknad = call.receive<Søknad>()
        logger.trace("Validerer søknad...")
        søknad.valider()
        logger.trace("Validering Ok.")
        call.respond(HttpStatusCode.Accepted)
    }
}
