package no.nav.omsorgspenger.soknadOverforeDager

import no.nav.helse.dusseldorf.ktor.core.ParameterType
import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.helse.dusseldorf.ktor.core.ValidationProblemDetails
import no.nav.helse.dusseldorf.ktor.core.Violation
import no.nav.omsorgspenger.soknad.erGyldigNorskIdentifikator
import no.nav.omsorgspenger.soknad.valider

val MIN_ANTALL_DAGER_MAN_KAN_OVERFØRE = 1
val MAX_ANTALL_DAGER_MAN_KAN_OVERFØRE = 999

internal fun SøknadOverføreDager.valider() {
    val violations: MutableSet<Violation> = mutableSetOf<Violation>()

    if (arbeidssituasjon.isEmpty()) {
        violations.add(
            Violation(
                parameterName = "arbeidssituasjon",
                parameterType = ParameterType.ENTITY,
                reason = "List over arbeidssituasjon kan ikke være tomt. Må inneholde minst 1 verdi",
                invalidValue = arbeidssituasjon
            )
        )
    }

    if (antallDager !in MIN_ANTALL_DAGER_MAN_KAN_OVERFØRE..MAX_ANTALL_DAGER_MAN_KAN_OVERFØRE) {
        violations.add(
            Violation(
                parameterName = "antallDager",
                parameterType = ParameterType.ENTITY,
                reason = "Tillatt antall dager man kan overføre må ligge mellom $MIN_ANTALL_DAGER_MAN_KAN_OVERFØRE og $MAX_ANTALL_DAGER_MAN_KAN_OVERFØRE dager.",
                invalidValue = antallDager
            )
        )
    }

    violations.addAll(medlemskap.valider())

    fosterbarn?.let { violations.addAll(validerFosterbarn(it)) }

    if (!harBekreftetOpplysninger) {
        violations.add(
            Violation(
                parameterName = "harBekreftetOpplysninger",
                parameterType = ParameterType.ENTITY,
                reason = "Opplysningene må bekreftes for å sende inn søknad.",
                invalidValue = harBekreftetOpplysninger
            )
        )
    }

    if (!harForståttRettigheterOgPlikter) {
        violations.add(
            Violation(
                parameterName = "harForståttRettigheterOgPlikter",
                parameterType = ParameterType.ENTITY,
                reason = "Må ha forstått rettigheter og plikter for å sende inn søknad.",
                invalidValue = harBekreftetOpplysninger
            )
        )
    }

    if(!fnrMottaker.erGyldigNorskIdentifikator()){
        violations.add(
            Violation(
                parameterName = "fnrMottaker",
                parameterType = ParameterType.ENTITY,
                reason = "Ikke gyldig norskIdentifikator på mottaker av dager",
                invalidValue = fnrMottaker
            )
        )
    }

    // TODO: Kan ikke skrues på før det har gått litt tid pga brukere med gammel frontend
//    if(navnMottaker == null){
//        violations.add(
//            Violation(
//                parameterName = "navnMottaker",
//                parameterType = ParameterType.ENTITY,
//                reason = "Navn på mottaker mangler",
//                invalidValue = navnMottaker
//            )
//        )
//    }

// Ser om det er noen valideringsfeil
    if (violations.isNotEmpty()) {
        throw Throwblem(ValidationProblemDetails(violations))
    }

}

private fun validerFosterbarn(fosterbarn: List<Fosterbarn>) = mutableSetOf<Violation>().apply {
    fosterbarn.mapIndexed { index, barn ->
        if (!barn.fødselsnummer.erGyldigNorskIdentifikator()) {
            add(
                Violation(
                    parameterName = "fosterbarn[$index].fødselsnummer",
                    parameterType = ParameterType.ENTITY,
                    reason = "Ikke gyldig fødselsnummer.",
                    invalidValue = barn.fødselsnummer
                )
            )
        }
    }
}
