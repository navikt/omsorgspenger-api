package no.nav.omsorgspengerapi.soknad.mottak

import java.time.LocalDate

data class Utenlandsopphold(
        val fraOgMed: LocalDate,
        val tilOgMed: LocalDate,
        val landkode: String,
        val landnavn: String
) {

}
