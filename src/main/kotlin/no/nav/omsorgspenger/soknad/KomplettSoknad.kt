package no.nav.omsorgspenger.soknad

import no.nav.k9.søknad.Søknad
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.vedlegg.Vedlegg
import java.time.ZonedDateTime

data class KomplettSoknad(
    val nyVersjon: Boolean,
    val språk: String,
    val søknadId: String,
    val mottatt: ZonedDateTime,
    val kroniskEllerFunksjonshemming: Boolean,
    val søker: Søker,
    val arbeidssituasjon: List<Arbeidssituasjon>? = null, //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    val barn: BarnDetaljer,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val sammeAdresse: Boolean?,
    val medlemskap: Medlemskap? = null, //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    val legeerklæring: List<Vedlegg>,
    val samværsavtale: List<Vedlegg>?,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val k9FormatSøknad: Søknad
)
