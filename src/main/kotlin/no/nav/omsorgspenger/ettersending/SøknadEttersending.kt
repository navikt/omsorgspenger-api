package no.nav.omsorgspenger.ettersending

import java.net.URL

data class SøknadEttersending(
    val språk: String,
    val vedlegg: List<URL>,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
    //TODO: Mer data
)