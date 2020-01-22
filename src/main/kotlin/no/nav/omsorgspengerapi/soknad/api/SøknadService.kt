package no.nav.omsorgspengerapi.soknad.api

import no.nav.helse.soker.Søker
import no.nav.helse.soker.validate
import no.nav.omsorgspengerapi.soker.api.SøkerService
import no.nav.omsorgspengerapi.soknad.mottak.KomplettSøknadDTO
import no.nav.omsorgspengerapi.soknad.mottak.SøknadMottakService
import no.nav.omsorgspengerapi.vedlegg.api.VedleggService
import no.nav.omsorgspengerapi.vedlegg.dokument.DocumentJsonDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URL
import java.time.ZonedDateTime

@Service
class SøknadService(
        private val søknadMottakService: SøknadMottakService,
        private val søkerService: SøkerService,
        private val vedleggService: VedleggService
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(SøknadService::class.java)
    }

    fun sendSoknad(søknad: Søknad): Mono<Unit> {
        log.info("Henter søker...")
        val søker = søkerService.getSøker()
        log.info("Søker hentet.")

        log.info("Validerer søker...")
        søker.subscribe { it.validate() }
        log.info("Søker validert.")

        log.info("Henter {} legeerklæringer...", søknad.legeerklæring.size)
        val legeerklæringsFiler: Flux<DocumentJsonDTO> = vedleggService.hentVedlegg(søknad.legeerklæring)
        legeerklæringsFiler.collectList().subscribe { log.info("Hentet {} legeerklaringsFiler.", it.size) }

        log.info("Henter {} samvarsavtaleFiler...", søknad.samværsavtale?.size)
        val samvarsavtaleFiler: Flux<DocumentJsonDTO>? = søknad.samværsavtale?.let {
            vedleggService.hentVedlegg(it)
        }
        if (samvarsavtaleFiler != null) {
            samvarsavtaleFiler.collectList().subscribe { log.info("Hentet {} samvarsavtaleFiler.", it.size) }
        }

        val vedleggsUrler = mutableListOf<URL>()
        vedleggsUrler.addAll(søknad.legeerklæring)
        vedleggsUrler.addAll(søknad.samværsavtale!!)

        val vedlegger: Mono<MutableList<DocumentJsonDTO>> = Flux.concat(legeerklæringsFiler, samvarsavtaleFiler).collectList()
        vedlegger.map {
            log.info("Validerer vedleggene...")
            it.validerVedleggene(vedleggsUrler)
            log.info("Vedleggene validert.")
        }

        log.info("Sender søknad for mottak")
        return søknadMottakService.sendSøknad(komplettSøknadDTO = søknad.TilKomplettSøknad(søker)).map { Unit }
    }
}

private fun Søknad.TilKomplettSøknad(søker: Mono<Søker>): KomplettSøknadDTO = KomplettSøknadDTO(
        nyVersjon = nyVersjon,
        språk = språk,
        mottatt = ZonedDateTime.now(),
        erYrkesaktiv = erYrkesaktiv,
        kroniskEllerFunksjonshemming = kroniskEllerFunksjonshemming,
        søker = søker,
        barn = barn,
        medlemskap = medlemskap,
        relasjonTilBarnet = relasjonTilBarnet,
        delerOmsorg = delerOmsorg,
        sammeAddresse = sammeAddresse,
        legeerklæring = legeerklæring,
        samværsavtale = samværsavtale,
        harBekreftetOpplysninger = harBekreftetOpplysninger,
        harForståttRettigheterOgPlikter = harForståttRettigheterOgPlikter

)
