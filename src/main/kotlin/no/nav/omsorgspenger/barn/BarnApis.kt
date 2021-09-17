package no.nav.omsorgspenger.barn

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.omsorgspenger.felles.BARN_URL
import no.nav.omsorgspenger.general.auth.IdTokenProvider
import no.nav.omsorgspenger.general.getCallId

fun Route.barnApis(
    barnService: BarnService,
    idTokenProvider: IdTokenProvider
) {

    get(BARN_URL) {
        call.respond(
            BarnResponse(
                barnService.hentNåværendeBarn(
                    idToken = idTokenProvider.getIdToken(call),
                    callId = call.getCallId()
                )
            )
        )
    }
}