package no.nav.omsorgspenger.ettersending

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

data class SøknadEttersending(
    val språk: String,
    val vedlegg: List<URL>,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val beskrivelse: String,
    val søknadstype: Søknadstype
)

enum class Søknadstype() {
    @JsonProperty("ukjent") UKJENT,
    @JsonProperty("pleiepenger") PLEIEPENGER,
    @JsonProperty("omsorgspenger") OMSORGSPENGER
}