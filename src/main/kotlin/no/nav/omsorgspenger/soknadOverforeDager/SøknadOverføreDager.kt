package no.nav.omsorgspenger.soknadOverforeDager

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.omsorgspenger.soknad.Medlemskap

data class SøknadOverføreDager(
    val språk: String,
    val antallDager: Int? = null,//TODO: Fjerne optional når kleint sender med
    val mottakerAvDagerNorskIdentifikator: String? = null, //TODO: Fjerne optional når kleint sender med
    val medlemskap: Medlemskap,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val arbeidssituasjon: List<Arbeidssituasjon>
)

enum class Arbeidssituasjon() {
    @JsonProperty("arbeidstaker") ARBEIDSTAKER,
    @JsonProperty("selvstendigNæringsdrivende") SELVSTENDIGNÆRINGSDRIVENDE,
    @JsonProperty("frilanser") FRILANSER
}