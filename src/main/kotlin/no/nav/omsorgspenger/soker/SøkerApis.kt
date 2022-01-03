package no.nav.omsorgspenger.soker

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.helse.dusseldorf.ktor.auth.IdTokenProvider
import no.nav.helse.dusseldorf.ktor.core.DefaultProblemDetails
import no.nav.helse.dusseldorf.ktor.core.respondProblemDetails
import no.nav.omsorgspenger.felles.SØKER_URL
import no.nav.omsorgspenger.general.getCallId
import no.nav.omsorgspenger.general.oppslag.TilgangNektetException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

private val logger = LoggerFactory.getLogger("no.nav.omsorgspenger.soker.SøkerApisKt.søkerApis")

fun Route.søkerApis(
    søkerService: SøkerService,
    idTokenProvider: IdTokenProvider
) {

    get(SØKER_URL) {
        try {
            call.respond(
                søkerService.getSoker(
                    idToken = idTokenProvider.getIdToken(call),
                    callId = call.getCallId()
                )
            )
        } catch (e: Exception) {
            when (e) {
                is TilgangNektetException -> call.respondTilgangNektetProblemDetail(logger, e)
                else -> throw e
            }
        }
    }
}

suspend fun ApplicationCall.respondTilgangNektetProblemDetail(logger: Logger, e: TilgangNektetException) =
    respondProblemDetails(
        logger = logger,
        problemDetails = DefaultProblemDetails(
            title = "tilgangskontroll-feil",
            status = 451,
            instance = URI(request.path()),
            detail = e.message
        )
    )

