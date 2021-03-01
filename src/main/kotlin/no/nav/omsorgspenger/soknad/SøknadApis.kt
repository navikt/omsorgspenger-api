package no.nav.omsorgspenger.soknad

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.omsorgspenger.general.auth.IdTokenProvider
import no.nav.omsorgspenger.general.getCallId
import no.nav.omsorgspenger.k9format.tilK9Format
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soker.validate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.time.ZonedDateTime

private val logger: Logger = LoggerFactory.getLogger("nav.soknadApis")

@KtorExperimentalLocationsAPI
fun Route.søknadApis(
    søknadService: SøknadService,
    idTokenProvider: IdTokenProvider,
    søkerService: SøkerService
) {

    @Location("/soknad")
    class sendSoknad

    post { _: sendSoknad ->
        val mottatt = ZonedDateTime.now(ZoneOffset.UTC)
        val idToken = idTokenProvider.getIdToken(call)
        val callId = call.getCallId()

        logger.trace("Mottatt ny søknad. Mapper søknad.")
        val søknad = call.receive<Søknad>()
        logger.trace("Søknad mappet. Validerer")

        logger.trace("Henter søker.")
        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)
        logger.trace("Søker hentet. Validerer søkeren.")
        søker.validate()

        logger.info("Mapper om søknad til k9format.")
        val k9FormatSøknad = søknad.tilK9Format(mottatt, søker)

        søknad.valider(k9FormatSøknad)
        logger.trace("Validering OK. Registrerer søknad.")

        søknadService.registrer(
            søknad = søknad,
            callId = call.getCallId(),
            idToken = idTokenProvider.getIdToken(call),
            k9FormatSøknad = k9FormatSøknad,
            søker = søker,
            mottatt = mottatt
        )

        logger.trace("Søknad registrert.")
        call.respond(HttpStatusCode.Accepted)
    }

    @Location("/soknad/valider")
    class validerSoknad

    post { _: validerSoknad ->
        val søknad = call.receive<Søknad>()
        logger.trace("Validerer søknad...")
        val mottatt = ZonedDateTime.now(ZoneOffset.UTC)
        val idToken = idTokenProvider.getIdToken(call)
        val callId = call.getCallId()

        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)

        val k9FormatSøknad = søknad.tilK9Format(mottatt, søker)
        søknad.valider(k9FormatSøknad)
        logger.trace("Validering Ok.")
        call.respond(HttpStatusCode.Accepted)
    }
}