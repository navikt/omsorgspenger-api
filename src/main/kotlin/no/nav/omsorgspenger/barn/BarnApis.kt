package no.nav.omsorgspenger.barn

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.helse.dusseldorf.ktor.auth.IdTokenProvider
import no.nav.omsorgspenger.felles.BARN_URL
import no.nav.omsorgspenger.general.getCallId
import no.nav.omsorgspenger.general.oppslag.TilgangNektetException
import no.nav.omsorgspenger.soker.respondTilgangNektetProblemDetail
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("no.nav.omsorgspenger.barn.BarnApisKt.barnApis")

fun Route.barnApis(
    barnService: BarnService,
    idTokenProvider: IdTokenProvider
) {

    get(BARN_URL) {
        try {
            call.respond(
                BarnResponse(
                    barnService.hentNåværendeBarn(
                        idToken = idTokenProvider.getIdToken(call),
                        callId = call.getCallId()
                    )
                )
            )
        } catch (e: Exception) {
            when(e) {
                is TilgangNektetException -> call.respondTilgangNektetProblemDetail(logger, e)
                else -> throw e
            }
        }
    }
}
