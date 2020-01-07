package no.nav.omsorgspengerapi.soknad.mottak

import java.util.*

data class Utenlandsopphold(
        val fraOgMed: Date,
        val tilOgMed: Date,
        val landkode: String,
        val landnavn: String
) {

}
