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

class SøknadEttersendingService(
    private val omsorgpengesøknadMottakGateway: OmsorgpengesøknadMottakGateway,
    private val søkerService: SøkerService,
    private val vedleggService: VedleggService
){
    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(SøknadService::class.java)
    }

    suspend fun registrer(
        søknadEttersending: SøknadEttersending,
        idToken: IdToken,
        callId: CallId
    ){
        logger.info("Registrerer søknad for ettersending. Henter søker")
        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)

        logger.info("Søker hentet. Validerer søker.")
        søker.validate()

        //TODO: Her må vedlegg hentes og validers

        logger.info("Legger søknad for ettersending til prosessering")

        val komplettSøknadEttersending = KomplettSøknadEttersending(
            språk = søknadEttersending.språk,
            mottatt = ZonedDateTime.now(ZoneOffset.UTC),
            harForståttRettigheterOgPlikter = søknadEttersending.harForståttRettigheterOgPlikter,
            harBekreftetOpplysninger = søknadEttersending.harBekreftetOpplysninger
        )

        omsorgpengesøknadMottakGateway.leggTilProsesseringEttersending(
            soknad = komplettSøknadEttersending,
            callId = callId
        )

        logger.trace("Søknad for ettersending lagt til prosessering.")
    }
}