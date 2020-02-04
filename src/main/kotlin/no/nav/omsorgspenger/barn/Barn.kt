package no.nav.omsorgspenger.barn

import java.time.LocalDate

data class BarnResponse(
    val barn: List<Barn>
)

data class Barn (
    val fødselsdato: LocalDate,
    val fødselsnummer: String? = null,
    val navn: String?,
    val aktørId: String?
)