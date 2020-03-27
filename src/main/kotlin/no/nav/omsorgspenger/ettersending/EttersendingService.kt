package no.nav.omsorgspenger.ettersending

import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.general.auth.IdToken
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soker.validate
import no.nav.omsorgspenger.soknad.OmsorgpengesøknadMottakGateway
import no.nav.omsorgspenger.soknad.SøknadService
import no.nav.omsorgspenger.vedlegg.VedleggService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.time.ZonedDateTime

class EttersendingService(
    private val omsorgpengesøknadMottakGateway: OmsorgpengesøknadMottakGateway,
    private val søkerService: SøkerService,
    private val vedleggService: VedleggService
){
    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(SøknadService::class.java)
    }

    suspend fun registrer(
        ettersending: Ettersending,
        idToken: IdToken,
        callId: CallId
    ){
        logger.info("Registrerer ettersending. Henter søker")
        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)

        logger.info("Søker hentet. Validerer søker.")
        søker.validate()
        logger.info("Søker Validert.")

        logger.trace("Henter ${ettersending.vedlegg.size} vedlegg.")
        val vedlegg = vedleggService.hentVedlegg(
            idToken = idToken,
            vedleggUrls = ettersending.vedlegg,
            callId = callId
        )

        logger.trace("Vedlegg hentet. Validerer vedlegg.")
        vedlegg.validerVedlegg(ettersending.vedlegg)
        logger.info("Vedlegg validert")

        logger.info("Legger ettersending til prosessering")

        val komplettEttersending = KomplettEttersending(
            søker = søker,
            språk = ettersending.språk,
            mottatt = ZonedDateTime.now(ZoneOffset.UTC),
            vedlegg = vedlegg,
            harForståttRettigheterOgPlikter = ettersending.harForståttRettigheterOgPlikter,
            harBekreftetOpplysninger = ettersending.harBekreftetOpplysninger,
            beskrivelse = ettersending.beskrivelse,
            søknadstype = ettersending.søknadstype
        )

        omsorgpengesøknadMottakGateway.leggTilProsesseringEttersending(
            ettersending = komplettEttersending,
            callId = callId
        )

        logger.trace("Ettersending lagt til prosessering. Sletter vedlegg.")

        vedleggService.slettVedlegg(
            vedleggUrls = ettersending.vedlegg,
            callId = callId,
            idToken = idToken
        )

        logger.trace("Vedlegg slettet.")
    }
}