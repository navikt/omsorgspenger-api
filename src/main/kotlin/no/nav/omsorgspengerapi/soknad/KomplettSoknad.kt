package no.nav.omsorgspengerapi.soknad

import no.nav.omsorgspengerapi.vedlegg.Vedlegg
import no.nav.omsorgspengerapi.barn.Barn
import no.nav.omsorgspengerapi.soker.Søker
import java.time.ZonedDateTime

data class KomplettSoknad(
    val nyVersjon: Boolean,
    val språk: String,
    val mottatt : ZonedDateTime,
    val erYrkesaktiv: Boolean,
    val kroniskEllerFunksjonshemming: Boolean,
    val barn: Barn,
    val søker: Søker,
    val sammeAddresse: Boolean?,
    val delerOmsorg: Boolean?,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val legeerklæring: List<Vedlegg>,
    val samværsavtale: List<Vedlegg>?,
    val medlemskap: Medlemskap,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
)