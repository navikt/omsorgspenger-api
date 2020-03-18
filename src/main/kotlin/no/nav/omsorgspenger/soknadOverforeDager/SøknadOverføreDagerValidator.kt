package no.nav.omsorgspenger.soknadOverforeDager

import no.nav.helse.dusseldorf.ktor.core.ParameterType
import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.helse.dusseldorf.ktor.core.ValidationProblemDetails
import no.nav.helse.dusseldorf.ktor.core.Violation
import no.nav.omsorgspenger.soknad.erGyldigNorskIdentifikator
import no.nav.omsorgspenger.soknad.valider

internal fun SøknadOverføreDager.valider() {
    val violations: MutableSet<Violation> = mutableSetOf<Violation>()

    if(!harSamfunnskritiskJobb){
        violations.add(
            Violation(
                parameterName = "harSamfunnskritiskJobb",
                parameterType = ParameterType.ENTITY,
                reason = "harSamfunnskritiskJobb må være satt til true",
                invalidValue = harSamfunnskritiskJobb
            )
        )
    }

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

    violations.addAll(medlemskap.valider())

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

    //TODO: Trenger validering på antall dager(Kanskje maks 10? Avventer svar fra analyse)

    if(!mottakerAvDagerNorskIdentifikator.erGyldigNorskIdentifikator()){
        violations.add(
            Violation(
                parameterName = "mottakerAvDagerNorskIdentifikator",
                parameterType = ParameterType.ENTITY,
                reason = "Ikke gyldig norskIdentifikator på mottaker av dager",
                invalidValue = mottakerAvDagerNorskIdentifikator
            )
        )
    }

// Ser om det er noen valideringsfeil
    if (violations.isNotEmpty()) {
        throw Throwblem(ValidationProblemDetails(violations))
    }

}