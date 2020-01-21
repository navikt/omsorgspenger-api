package no.nav.omsorgspengerapi.barn.lookup

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class BarnOppslagDTO(
        val fødselsdato: LocalDate,
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
        @JsonProperty("aktør_id") val aktørId: String
)