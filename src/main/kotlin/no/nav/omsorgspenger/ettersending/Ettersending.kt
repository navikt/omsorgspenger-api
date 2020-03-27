package no.nav.omsorgspenger.ettersending

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.omsorgspenger.soknad.Medlemskap
import java.net.URL

data class Ettersending(
    val språk: String,
    val vedlegg: List<URL>,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val beskrivelse: String,
    val søknadstype: String
)
