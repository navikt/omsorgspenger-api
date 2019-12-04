package no.nav.omsorgspengerapi.barn.lookup

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ChildLookupDTO (
        @JsonProperty("fødselsdato") @JsonFormat(pattern = "yyyy-MM-dd") val fodselsdato: LocalDate,
        @JsonProperty("fornavn") val fornavn: String,
        @JsonProperty("mellomnavn") val mellomnavn: String? = null,
        @JsonProperty("etternavn") val etternavn: String,
        @JsonProperty("aktør_id") val aktoerId: String
)