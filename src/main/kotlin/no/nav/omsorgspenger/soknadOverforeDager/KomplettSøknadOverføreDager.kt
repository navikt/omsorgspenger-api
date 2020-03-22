package no.nav.omsorgspenger.soknadOverforeDager

import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soknad.Medlemskap
import java.time.ZonedDateTime

data class KomplettSøknadOverføreDager (
    val mottatt: ZonedDateTime,
    val søker: Søker,
    val språk: String,
    val antallDager: Int,
    val fnrMottaker: String,
    val medlemskap: Medlemskap,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val arbeidssituasjon: List<Arbeidssituasjon>,
    val antallBarn: Int
)


