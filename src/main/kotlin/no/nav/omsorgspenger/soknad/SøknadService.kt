package no.nav.omsorgspenger.soknad

import no.nav.omsorgspenger.barn.Barn
import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.general.auth.IdToken
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soker.validate
import no.nav.omsorgspenger.vedlegg.VedleggService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.time.ZonedDateTime


class SøknadService(private val omsorgpengesøknadMottakGateway: OmsorgpengesøknadMottakGateway,
                    private val søkerService: SøkerService,
                    private val vedleggService: VedleggService
) {

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(SøknadService::class.java)
    }

    suspend fun registrer(
        søknad: Søknad,
        idToken: IdToken,
        callId: CallId
    ) {
        logger.trace("Registrerer søknad. Henter søker")
        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)

        logger.trace("Søker hentet. Validerer om søkeren.")
        søker.validate()

        logger.trace("Validert Søker. Henter ${søknad.legeerklæring.size} legeerklæring.")
        val legeerklæring = vedleggService.hentVedlegg(
            idToken = idToken,
            vedleggUrls = søknad.legeerklæring,
            callId = callId
        )

        logger.trace("Validert Søker. Henter ${søknad.legeerklæring.size} legeerklæring.")
        val samværsavtale = vedleggService.hentVedlegg(
            idToken = idToken,
            vedleggUrls = søknad.samværsavtale!!,
            callId = callId
        )

        logger.trace("Vedlegg hentet. Validerer vedleggene.")
        legeerklæring.validerLegeerklæring(søknad.legeerklæring)
        samværsavtale.validerSamværsavtale(søknad.samværsavtale)
        val alleVedlegg = listOf(*legeerklæring.toTypedArray(), *samværsavtale.toTypedArray())
        alleVedlegg.validerTotalStørresle()

        logger.trace("Legger søknad til prosessering")

        val komplettSoknad = KomplettSoknad(
            språk = søknad.språk,
            mottatt = ZonedDateTime.now(ZoneOffset.UTC),
            søker = søker,
            barn = Barn(
                fødselsdato = søknad.barn.fødselsdato,
                aktørId = søknad.barn.aktørId,
                navn = søknad.barn.navn
            ),
            legeerklæring = legeerklæring,
            samværsavtale = samværsavtale,
            medlemskap = søknad.medlemskap,
            relasjonTilBarnet = søknad.relasjonTilBarnet,
            harBekreftetOpplysninger = søknad.harBekreftetOpplysninger,
            harForståttRettigheterOgPlikter = søknad.harForståttRettigheterOgPlikter,
            delerOmsorg = søknad.delerOmsorg,
            erYrkesaktiv = søknad.erYrkesaktiv,
            kroniskEllerFunksjonshemming = søknad.kroniskEllerFunksjonshemming,
            nyVersjon = søknad.nyVersjon,
            sammeAddresse = søknad.sammeAddresse
        )

        omsorgpengesøknadMottakGateway.leggTilProsessering(
            soknad = komplettSoknad,
            callId = callId
        )

        logger.trace("Søknad lagt til prosessering. Sletter vedlegg.")

        vedleggService.slettVedleg(
            vedleggUrls = søknad.samværsavtale,
            callId = callId,
            idToken = idToken
        )

        vedleggService.slettVedleg(
            vedleggUrls = søknad.legeerklæring,
            callId = callId,
            idToken = idToken
        )

        logger.trace("Vedlegg slettet.")
    }
}

