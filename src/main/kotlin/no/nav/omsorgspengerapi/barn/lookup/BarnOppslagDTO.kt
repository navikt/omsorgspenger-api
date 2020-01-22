package no.nav.omsorgspengerapi.barn.lookup

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class BarnOppslagDTO(
        @JsonFormat(pattern = "yyyy-MM-dd") val fødselsdato: LocalDate,
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
        @JsonProperty("aktør_id") val aktørId: String
)

data class BarnOppslagRespons(val barn: List<BarnOppslagDTO>)