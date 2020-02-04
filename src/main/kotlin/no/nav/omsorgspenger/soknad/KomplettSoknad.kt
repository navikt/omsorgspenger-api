package no.nav.omsorgspenger.soknad

import no.nav.omsorgspenger.barn.Barn
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.vedlegg.Vedlegg
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