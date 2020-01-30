package no.nav.omsorgspengerapi.barn

import java.time.LocalDate

data class BarnResponse(
    val barn: List<Barn>
)

data class Barn (
    val fødselsdato: LocalDate,
    val fødselsnummer: String? = null,
    val fornavn: String,
    val mellomnavn: String? = null,
    val etternavn: String,
    val aktørId: String
)