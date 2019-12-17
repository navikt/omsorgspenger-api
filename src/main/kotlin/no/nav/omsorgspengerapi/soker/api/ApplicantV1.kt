package no.nav.helse.soker

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.omsorgspengerapi.soknad.api.ApplicationValidationException
import no.nav.omsorgspengerapi.soknad.api.ParameterType
import no.nav.omsorgspengerapi.soknad.api.Violation
import java.time.ZoneId
import java.time.LocalDate

private val ZONE_ID = ZoneId.of("Europe/Oslo")
private const val MYNDIG_ALDER = 18L

data class ApplicantV1 (
        @JsonProperty("aktør_id") val aktoer_id: String,
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String,
        @JsonProperty("fødselsdato")val fodselsdato: LocalDate
) {
    fun isLegal(fodselsdato: LocalDate) : Boolean {
        val myndighetsDato = fodselsdato.plusYears(MYNDIG_ALDER)
        val dagensDato = LocalDate.now(ZONE_ID)
        return myndighetsDato.isBefore(dagensDato) || myndighetsDato.isEqual(dagensDato)
    }
}

fun ApplicantV1.validate() {
    if (!isLegal(fodselsdato)) {
        throw ApplicationValidationException(message = "Applicant is not legal.", violations =  mutableSetOf(Violation(
                parameterName = "fodselsdato",
                parameterType = ParameterType.ENTITY,
                reason = "Applicant is not legal. Applicant must be 18 years or older.",
                invalidValue = null
        )))
    }
}

