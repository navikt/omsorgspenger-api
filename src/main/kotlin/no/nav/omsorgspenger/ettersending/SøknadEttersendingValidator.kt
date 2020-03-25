package no.nav.omsorgspenger.ettersending

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.helse.dusseldorf.ktor.core.ValidationProblemDetails
import no.nav.helse.dusseldorf.ktor.core.Violation

internal fun SøknadEttersending.valider() {
    val violations: MutableSet<Violation> = mutableSetOf<Violation>()

    //TODO:Validering

    if (violations.isNotEmpty()) {
        throw Throwblem(ValidationProblemDetails(violations))
    }
}