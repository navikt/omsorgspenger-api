package no.nav.omsorgspenger.soknadOverforeDager

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.omsorgspenger.soknad.Medlemskap

data class SøknadOverføreDager(
    val språk: String,
    val antallDager: Int,
    val fnrMottaker: String,
    val medlemskap: Medlemskap,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val arbeidssituasjon: List<Arbeidssituasjon>,
    val antallBarn: Int
)

enum class Arbeidssituasjon() {
    @JsonProperty("arbeidstaker") ARBEIDSTAKER,
    @JsonProperty("selvstendigNæringsdrivende") SELVSTENDIGNÆRINGSDRIVENDE,
    @JsonProperty("frilanser") FRILANSER
}