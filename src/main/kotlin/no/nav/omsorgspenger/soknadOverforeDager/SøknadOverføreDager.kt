package no.nav.omsorgspenger.soknadOverforeDager

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.omsorgspenger.soknad.Medlemskap

data class SøknadOverføreDager(
    val språk: String,
    val antallDager: Int,
    val mottakerAvDager: Int,
    val medlemskap: Medlemskap,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val arbeidssituasjon: List<Arbeidssituasjon>
)

enum class Arbeidssituasjon() {
    @JsonProperty("Arbeidstaker") ARBEIDSTAKER,
    @JsonProperty("Selvstendig Næringsdrivende") SELVSTENDIGNÆRINGSDRIVENDE,
    @JsonProperty("Frilans") FRILANSER
}