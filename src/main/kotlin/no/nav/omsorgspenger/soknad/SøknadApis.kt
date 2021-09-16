package no.nav.omsorgspenger.soknad

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.omsorgspenger.barn.BarnService
import no.nav.omsorgspenger.felles.SØKNAD_URL
import no.nav.omsorgspenger.felles.VALIDERING_URL
import no.nav.omsorgspenger.felles.formaterStatuslogging
import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.general.auth.IdToken
import no.nav.omsorgspenger.general.auth.IdTokenProvider
import no.nav.omsorgspenger.general.getCallId
import no.nav.omsorgspenger.general.getMetadata
import no.nav.omsorgspenger.k9format.tilK9Format
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.time.ZonedDateTime

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
        val mottatt = ZonedDateTime.now(ZoneOffset.UTC)
        val idToken = idTokenProvider.getIdToken(call)
        val callId = call.getCallId()

        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)
        val barn = resolveBarn(søknad, barnService, idToken, callId)

        søknad.oppdaterBarnsIdentitetsnummer(barn)

        val k9FormatSøknad = søknad.tilK9Format(mottatt, søker)
        søknad.valider(k9FormatSøknad)
        logger.trace("Validering Ok.")
        call.respond(HttpStatusCode.Accepted)
    }
}

suspend fun resolveBarn(
    søknad: Søknad,
    barnService: BarnService,
    idToken: IdToken,
    callId: CallId
): BarnDetaljer = when {
    // Gjelder annet barn enn fra oppslag
    søknad.barn.aktørId.isNullOrBlank() -> søknad.barn

    // Oppslagsbarn
    else -> {
        val barn = barnService.hentNaaverendeBarn(
            idToken = idToken,
            callId = callId
        ).map {
            BarnDetaljer(
                norskIdentifikator = it.identitetsnummer,
                fødselsdato = it.fødselsdato,
                aktørId = it.aktørId,
                navn = when (it.mellomnavn) {
                    null, "" -> "${it.fornavn} ${it.etternavn}"
                    else -> "${it.fornavn} ${it.mellomnavn} ${it.etternavn}"
                }
            )
        }
            .firstOrNull { it.aktørId == søknad.barn.aktørId }
        barn ?: throw IllegalStateException("Kunne ikke fimme barnets aktørId blant liste over oppslagsbarn.") // Burde ikke forekomme
    }
}