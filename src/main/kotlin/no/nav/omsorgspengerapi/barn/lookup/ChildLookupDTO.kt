package no.nav.omsorgspengerapi.barn.lookup

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ChildLookupDTO(
        @JsonProperty("fødselsdato") @JsonFormat(pattern = "yyyy-MM-dd") val fodselsdato: LocalDate,
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
        @JsonProperty("aktør_id") val aktoerId: String
)