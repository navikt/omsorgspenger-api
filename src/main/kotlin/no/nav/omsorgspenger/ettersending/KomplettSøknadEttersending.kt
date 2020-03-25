package no.nav.omsorgspenger.ettersending

import no.nav.omsorgspenger.vedlegg.Vedlegg
import java.time.ZonedDateTime

data class KomplettSøknadEttersending (
    val språk: String,
    val mottatt: ZonedDateTime,
    val vedlegg: List<Vedlegg>,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
)
