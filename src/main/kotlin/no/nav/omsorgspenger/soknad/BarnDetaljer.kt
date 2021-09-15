package no.nav.omsorgspenger.soknad

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class BarnDetaljer(
    val norskIdentifikator: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val fødselsdato: LocalDate? = null,
    val aktørId: String? = null,
    val navn: String
) {
    override fun toString(): String {
        return "BarnDetaljer(aktoerId=${aktørId}, navn=${navn}, fodselsdato=${fødselsdato}"
    }
}