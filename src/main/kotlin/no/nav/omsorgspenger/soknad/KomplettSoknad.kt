package no.nav.omsorgspenger.soknad

import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.vedlegg.Vedlegg
import java.time.ZonedDateTime

data class KomplettSoknad(
    val nyVersjon: Boolean,
    val språk: String,
    val mottatt: ZonedDateTime,
    val kroniskEllerFunksjonshemming: Boolean,
    val søker: Søker,
    val arbeidssituasjon: List<String>,
    val barn: BarnDetaljer,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val sammeAdresse: Boolean?,
    val medlemskap: Medlemskap,
    val legeerklæring: List<Vedlegg>,
    val samværsavtale: List<Vedlegg>?,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
)