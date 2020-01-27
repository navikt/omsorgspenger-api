package no.nav.omsorgspengerapi.soknad.mottak

import no.nav.helse.soker.Søker
import no.nav.omsorgspengerapi.barn.api.Barn
import no.nav.omsorgspengerapi.soknad.api.Medlemskap
import no.nav.omsorgspengerapi.soknad.api.SøkerBarnRelasjon
import no.nav.omsorgspengerapi.vedlegg.api.VedleggJson
import java.time.ZonedDateTime

data class KomplettSøknadDTO(
        val nyVersjon: Boolean,
        val språk: String,
        val mottatt: ZonedDateTime,
        val erYrkesaktiv: Boolean,
        val kroniskEllerFunksjonshemming: Boolean,
        val barn: Barn,
        val søker: Søker,
        val sammeAddresse: Boolean?,
        val delerOmsorg: Boolean?,
        val relasjonTilBarnet: SøkerBarnRelasjon? = null,
        val legeerklæring: List<VedleggJson>,
        val samværsavtale: List<VedleggJson>? = listOf(),
        val medlemskap: Medlemskap,
        val harForståttRettigheterOgPlikter: Boolean,
        val harBekreftetOpplysninger: Boolean
)