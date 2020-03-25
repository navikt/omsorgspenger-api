package no.nav.omsorgspenger.ettersending

import java.time.ZonedDateTime

data class KomplettSøknadEttersending (
    val språk: String,
    val mottatt: ZonedDateTime,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
)
