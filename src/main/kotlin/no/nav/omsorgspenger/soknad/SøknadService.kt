package no.nav.omsorgspenger.soknad

import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.general.auth.IdToken
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.vedlegg.DokumentEier
import no.nav.omsorgspenger.vedlegg.VedleggService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime


class SøknadService(
    private val omsorgpengesøknadMottakGateway: OmsorgpengesøknadMottakGateway,
    private val vedleggService: VedleggService
) {

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(SøknadService::class.java)
    }

    suspend fun registrer(
        søknad: Søknad,
        idToken: IdToken,
        callId: CallId,
        k9FormatSøknad: no.nav.k9.søknad.Søknad,
        søker: Søker,
        mottatt: ZonedDateTime
    ) {
        logger.info("Registrerer søknad")

        logger.info("Henter ${søknad.legeerklæring.size} legeerklæringsvedlegg.")
        val legeerklæring = vedleggService.hentVedlegg(
            idToken = idToken,
            vedleggUrls = søknad.legeerklæring,
            callId = callId,
            eier = DokumentEier(søker.fødselsnummer)
        )

        søknad.samværsavtale?.let { logger.info("Henter ${søknad.samværsavtale.size} samværsavtalevedlegg.") }
        val samværsavtale = when {
            !søknad.samværsavtale.isNullOrEmpty() -> {
                val samværsavtalevedlegg = vedleggService.hentVedlegg(
                    idToken = idToken,
                    vedleggUrls = søknad.samværsavtale,
                    callId = callId,
                    eier = DokumentEier(søker.fødselsnummer)
                )
                logger.info("Hentet ${samværsavtalevedlegg.size} samværsavtalevedlegg.")
                samværsavtalevedlegg
            }
            else -> listOf()
        }

        logger.info("Vedlegg hentet. Validerer vedleggene.")
        legeerklæring.validerLegeerklæring(søknad.legeerklæring)
        søknad.samværsavtale?.let { samværsavtale.validerSamværsavtale(it) }
        val alleVedlegg = listOf(*legeerklæring.toTypedArray(), *samværsavtale.toTypedArray())
        alleVedlegg.validerTotalStørresle()

        logger.info("Legger søknad til prosessering")

        val komplettSoknad = KomplettSoknad(
            språk = søknad.språk,
            mottatt = mottatt,
            søknadId = søknad.søknadId,
            søker = søker,
            barn = BarnDetaljer(
                fødselsdato = søknad.barn.fødselsdato,
                aktørId = søknad.barn.aktørId,
                navn = søknad.barn.navn,
                norskIdentifikator = søknad.barn.norskIdentifikator
            ),
            legeerklæring = legeerklæring,
            samværsavtale = samværsavtale,
            relasjonTilBarnet = søknad.relasjonTilBarnet,
            harBekreftetOpplysninger = søknad.harBekreftetOpplysninger,
            harForståttRettigheterOgPlikter = søknad.harForståttRettigheterOgPlikter,
            kroniskEllerFunksjonshemming = søknad.kroniskEllerFunksjonshemming,
            nyVersjon = søknad.nyVersjon,
            sammeAdresse = søknad.sammeAdresse,
            k9FormatSøknad = k9FormatSøknad
        )

        omsorgpengesøknadMottakGateway.leggTilProsessering(
            soknad = komplettSoknad,
            callId = callId
        )

        logger.trace("Søknad lagt til mottak")
    }
}
