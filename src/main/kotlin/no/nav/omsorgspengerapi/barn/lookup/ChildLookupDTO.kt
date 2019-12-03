package no.nav.omsorgspengerapi.barn.lookup

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ChildLookupDTO (
        @JsonFormat(pattern = "yyyy-MM-dd") val fodselsdato: LocalDate,
        @JsonProperty val fornavn: String,
        @JsonProperty val mellomnavn: String? = null,
        @JsonProperty val etternavn: String,
        @JsonProperty val aktoerId: String
)