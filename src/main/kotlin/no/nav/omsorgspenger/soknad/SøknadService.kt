package no.nav.omsorgspenger.soknad

import no.nav.k9.kafka.KafkaProducer
import no.nav.k9.kafka.Metadata
import no.nav.omsorgspenger.barn.BarnService
import no.nav.omsorgspenger.felles.formaterStatuslogging
import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.general.auth.IdToken
import no.nav.omsorgspenger.k9format.tilK9Format
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soker.validate
import no.nav.omsorgspenger.vedlegg.DokumentEier
import no.nav.omsorgspenger.vedlegg.VedleggService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.time.ZoneOffset
import java.time.ZonedDateTime


class SøknadService(
    private val vedleggService: VedleggService,
    private val søkerService: SøkerService,
    private val barnService: BarnService,
    private val kafkaProducer: KafkaProducer,
    private val k9MellomLagringIngress: URI,
    ) {

    private val logger: Logger = LoggerFactory.getLogger(SøknadService::class.java)

    suspend fun registrer(
        søknad: Søknad,
        idToken: IdToken,
        callId: CallId,
        metadata: Metadata
    ) {
        logger.info(formaterStatuslogging(søknad.søknadId, "registreres"))
        val mottatt = ZonedDateTime.now(ZoneOffset.UTC)

        val søker: Søker = søkerService.getSoker(idToken = idToken, callId = callId)
        søker.validate()

        val barn = resolveBarn(søknad, barnService, idToken, callId)
        søknad.oppdaterBarnsIdentitetsnummer(barn)

        val k9Format = søknad.tilK9Format(mottatt, søker)
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

        val komplettSoknad = søknad.tilKomplettSøknad(mottatt, søker, k9Format, k9MellomLagringIngress)

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
