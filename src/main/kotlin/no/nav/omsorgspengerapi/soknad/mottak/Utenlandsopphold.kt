package no.nav.omsorgspengerapi.soknad.mottak

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class Utenlandsopphold(
        @JsonFormat(pattern = "yyyy-MM-dd") val fraOgMed: LocalDate,
        @JsonFormat(pattern = "yyyy-MM-dd") val tilOgMed: LocalDate,
        val landkode: String,
        val landnavn: String
) {

}
