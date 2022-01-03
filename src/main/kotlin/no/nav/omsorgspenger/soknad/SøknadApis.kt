package no.nav.omsorgspenger.soknad

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.helse.dusseldorf.ktor.auth.IdTokenProvider
import no.nav.omsorgspenger.barn.BarnService
import no.nav.omsorgspenger.felles.SØKNAD_URL
import no.nav.omsorgspenger.felles.VALIDERING_URL
import no.nav.omsorgspenger.felles.formaterStatuslogging
import no.nav.omsorgspenger.general.getCallId
import no.nav.omsorgspenger.general.getMetadata
import no.nav.omsorgspenger.k9format.tilK9Format
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("nav.soknadApis")

fun Route.søknadApis(
    søknadService: SøknadService,
    idTokenProvider: IdTokenProvider,
    søkerService: SøkerService,
    barnService: BarnService
) {

    post(SØKNAD_URL) {
        val søknad = call.receive<Søknad>()
        logger.info(formaterStatuslogging(søknad.søknadId, "mottatt"))

        søknadService.registrer(
            søknad = søknad,
            callId = call.getCallId(),
            idToken = idTokenProvider.getIdToken(call),
            metadata = call.getMetadata()
        )

        call.respond(HttpStatusCode.Accepted)
    }

    post(VALIDERING_URL) {
        val søknad = call.receive<Søknad>()
        logger.trace("Validerer søknad...")
        val idToken = idTokenProvider.getIdToken(call)
        val callId = call.getCallId()

        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)

        if(søknad.barn.norskIdentifikator.isNullOrBlank()){
            val barnMedNorskIdentifikator = barnService.hentNåværendeBarn(idToken, callId)
            søknad oppdaterBarnsNorskIdentifikatorFra barnMedNorskIdentifikator
        }

        val k9FormatSøknad = søknad.tilK9Format(søker)
        søknad.valider(k9FormatSøknad)
        logger.trace("Validering Ok.")
        call.respond(HttpStatusCode.Accepted)
    }
}