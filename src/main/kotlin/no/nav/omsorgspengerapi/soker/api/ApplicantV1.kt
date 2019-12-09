package no.nav.helse.soker

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ApplicantV1 (
        @JsonProperty("aktør_id") val aktoer_id: String,
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String,
        @JsonProperty("fødselsdato")val fodselsdato: LocalDate
)

