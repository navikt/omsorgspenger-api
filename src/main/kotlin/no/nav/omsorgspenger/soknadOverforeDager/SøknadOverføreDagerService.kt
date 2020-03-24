package no.nav.omsorgspenger.soknadOverforeDager;

import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.general.auth.IdToken
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soker.validate
import no.nav.omsorgspenger.soknad.OmsorgpengesøknadMottakGateway
import no.nav.omsorgspenger.soknad.SøknadService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.time.ZonedDateTime

class SøknadOverføreDagerService(
    private val omsorgpengesøknadMottakGateway: OmsorgpengesøknadMottakGateway,
    private val søkerService: SøkerService
){
    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(SøknadService::class.java)
    }

    suspend fun registrer(
        søknadOverføreDager: SøknadOverføreDager,
        idToken: IdToken,
        callId: CallId
    ){
        logger.info("Registrerer søknad for overføring av dager. Henter søker")
        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)

        logger.info("Søker hentet. Validerer søker.")
        søker.validate()

        logger.info("Legger søknad for overføring av dager til prosessering")

        val komplettSøknadOverføreDager = KomplettSøknadOverføreDager(
            språk = søknadOverføreDager.språk,
            mottatt = ZonedDateTime.now(ZoneOffset.UTC),
            søker = søker,
            antallDager = søknadOverføreDager.antallDager,
            fnrMottaker = søknadOverføreDager.fnrMottaker,
            medlemskap = søknadOverføreDager.medlemskap,
            harForståttRettigheterOgPlikter = søknadOverføreDager.harForståttRettigheterOgPlikter,
            harBekreftetOpplysninger = søknadOverføreDager.harBekreftetOpplysninger,
            arbeidssituasjon = søknadOverføreDager.arbeidssituasjon,
            antallBarn = søknadOverføreDager.antallBarn,
            fosterbarn = søknadOverføreDager.fosterbarn
        )

        omsorgpengesøknadMottakGateway.leggTilProsesseringOverføreDager(
            soknad = komplettSøknadOverføreDager,
            callId = callId
        )

        logger.trace("Søknad for overføring av dager lagt til prosessering.")
    }
}