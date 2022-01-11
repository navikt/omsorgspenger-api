package no.nav.omsorgspenger.soknad

import no.nav.k9.søknad.Søknad
import no.nav.omsorgspenger.soker.Søker
import java.time.ZonedDateTime

data class KomplettSøknad(
    val nyVersjon: Boolean,
    val språk: String,
    val søknadId: String,
    val mottatt: ZonedDateTime,
    val kroniskEllerFunksjonshemming: Boolean,
    val søker: Søker,
    val barn: Barn,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val sammeAdresse: Boolean?,
    var legeerklæringVedleggId: List<String>,
    var samværsavtaleVedleggId: List<String>,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val k9FormatSøknad: Søknad
)