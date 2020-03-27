package no.nav.omsorgspenger.soknadEttersending

import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soknad.Medlemskap
import no.nav.omsorgspenger.vedlegg.Vedlegg
import java.time.ZonedDateTime

data class KomplettSøknadEttersending (
    val søker: Søker,
    val språk: String,
    val mottatt: ZonedDateTime,
    val vedlegg: List<Vedlegg>,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val beskrivelse: String,
    val søknadstype: Søknadstype,
    val medlemskap: Medlemskap
)
