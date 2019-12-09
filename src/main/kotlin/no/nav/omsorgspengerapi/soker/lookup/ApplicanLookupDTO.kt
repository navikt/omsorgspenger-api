package no.nav.omsorgspengerapi.soker.lookup

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ApplicanLookupDTO(
        @JsonProperty("aktør_id") val aktoer_id: String,
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String,
        @JsonProperty("fødselsdato")val fodselsdato: LocalDate
)
