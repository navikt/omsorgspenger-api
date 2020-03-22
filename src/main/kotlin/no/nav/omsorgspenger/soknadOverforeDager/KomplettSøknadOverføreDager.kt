package no.nav.omsorgspenger.soknadOverforeDager

import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soknad.Medlemskap
import java.time.ZonedDateTime

data class KomplettSøknadOverføreDager (
    val mottatt: ZonedDateTime,
    val søker: Søker,
    val språk: String,
    val antallDager: Int? = null,//TODO: Fjerne optional når kleint sender med
    val mottakerAvDagerNorskIdentifikator: String? = null,//TODO: Fjerne optional når kleint sender med
    val medlemskap: Medlemskap,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val arbeidssituasjon: List<Arbeidssituasjon>
)


