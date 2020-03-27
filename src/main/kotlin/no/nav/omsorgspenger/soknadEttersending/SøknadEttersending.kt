package no.nav.omsorgspenger.soknadEttersending

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.omsorgspenger.soknad.Medlemskap
import java.net.URL

data class SøknadEttersending(
    val språk: String,
    val vedlegg: List<URL>,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val beskrivelse: String,
    val søknadstype: Søknadstype,
    val medlemskap: Medlemskap
)

enum class Søknadstype() {
    @JsonProperty("ukjent") UKJENT,
    @JsonProperty("pleiepenger") PLEIEPENGER,
    @JsonProperty("omsorgspenger") OMSORGSPENGER
}