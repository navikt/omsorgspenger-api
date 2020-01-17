package no.nav.omsorgspengerapi.soker.lookup

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class SøkerOppslagsDTO(
        @JsonProperty("aktør_id") val aktørId: String,
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String,
        val fødselsdato: LocalDate
)
