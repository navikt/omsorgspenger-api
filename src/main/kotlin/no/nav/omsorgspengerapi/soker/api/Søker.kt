package no.nav.helse.soker

import no.nav.omsorgspengerapi.soknad.api.ApplicationValidationException
import no.nav.omsorgspengerapi.soknad.api.ParameterType
import no.nav.omsorgspengerapi.soknad.api.Violation
import java.time.LocalDate
import java.time.ZoneId

private val ZONE_ID = ZoneId.of("Europe/Oslo")
private const val MYNDIG_ALDER = 18L

data class Søker(
        val aktørId: String,
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String,
        val fødselsdato: LocalDate
) {
    fun isLegal(fødselsdato: LocalDate): Boolean {
        val myndighetsDato = fødselsdato.plusYears(MYNDIG_ALDER)
        val dagensDato = LocalDate.now(ZONE_ID)
        return myndighetsDato.isBefore(dagensDato) || myndighetsDato.isEqual(dagensDato)
    }
}

fun Søker.validate() {
    if (!isLegal(fødselsdato)) {
        throw ApplicationValidationException(message = "Applicant is not legal.", violations =  mutableSetOf(Violation(
                parameterName = "fødselsdato",
                parameterType = ParameterType.ENTITY,
                reason = "Søker er under 18 år, og dermed ikke myndig.",
                invalidValue = fødselsdato
        )))
    }
}

