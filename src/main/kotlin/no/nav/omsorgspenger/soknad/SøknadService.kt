package no.nav.omsorgspenger.soknad

import no.nav.helse.dusseldorf.ktor.auth.IdToken
import no.nav.k9.kafka.KafkaProducer
import no.nav.k9.kafka.Metadata
import no.nav.omsorgspenger.barn.BarnService
import no.nav.omsorgspenger.felles.formaterStatuslogging
import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.k9format.tilK9Format
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soker.validate
import no.nav.omsorgspenger.vedlegg.DokumentEier
import no.nav.omsorgspenger.vedlegg.VedleggService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SøknadService(
    private val vedleggService: VedleggService,
    private val søkerService: SøkerService,
    private val barnService: BarnService,
    private val kafkaProducer: KafkaProducer
    ) {

    private val logger: Logger = LoggerFactory.getLogger(SøknadService::class.java)

    suspend fun registrer(
        søknad: Søknad,
        idToken: IdToken,
        callId: CallId,
        metadata: Metadata
    ) {
        logger.info(formaterStatuslogging(søknad.søknadId, "registreres"))

        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)
        søker.validate()

        if(søknad.barn.norskIdentifikator.isNullOrBlank()){
            val barnMedNorskIdentifikator = barnService.hentNåværendeBarn(idToken, callId)
            søknad oppdaterBarnsNorskIdentifikatorFra barnMedNorskIdentifikator
        }

        val k9Format = søknad.tilK9Format(søker)
        søknad.valider(k9Format)

        logger.info("Validerer ${søknad.legeerklæring.size} legeerklæringsvedlegg.")
        val dokumentEier = DokumentEier(søker.fødselsnummer)
        val hentetLegeerklæring = vedleggService.hentVedlegg(søknad.legeerklæring, idToken, callId, dokumentEier)
        hentetLegeerklæring.validerVedlegg("legeerklæring", søknad.legeerklæring)

        søknad.samværsavtale?.let {
            logger.info("Validerer ${søknad.samværsavtale.size} samværsavtalevedlegg.")
            val hentetSamværsavtale = vedleggService.hentVedlegg(søknad.samværsavtale, idToken, callId, dokumentEier)
            hentetSamværsavtale.validerVedlegg("samværsavtale", søknad.samværsavtale)
        }

        logger.info("Persiterer legeerklæringvedlegg")
        vedleggService.persisterVedlegg(søknad.legeerklæring, callId, dokumentEier)

        søknad.samværsavtale?.let {
            logger.info("Persiterer samværsavtalevedlegg")
            vedleggService.persisterVedlegg(søknad.samværsavtale, callId, dokumentEier)
        }

        val komplettSoknad = søknad.tilKomplettSøknad(søker, k9Format)

        try {
            kafkaProducer.produserKafkaMelding(komplettSoknad, metadata)
        } catch (exception: Exception) {
            logger.info("Feilet ved å legge melding på Kafka. Fjerner hold på persisterte vedlegg")
            vedleggService.fjernHoldPåPersistertVedlegg(søknad.legeerklæring, callId, dokumentEier)
            søknad.samværsavtale?.let {
                vedleggService.fjernHoldPåPersistertVedlegg(søknad.samværsavtale, callId, dokumentEier)
            }
            throw MeldingRegistreringFeiletException("Feilet ved å legge melding på Kafka")
        }
    }
}

class MeldingRegistreringFeiletException(s: String) : Throwable(s)
