package no.nav.omsorgspenger.ettersending

data class SøknadEttersending(
    val språk: String,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
    //TODO: Mer data
)