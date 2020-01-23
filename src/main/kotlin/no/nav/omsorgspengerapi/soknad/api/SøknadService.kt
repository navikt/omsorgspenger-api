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

    fun sendSoknad(søknad: Søknad): Mono<SøknadId> {
        log.info("Henter søker...")
        val søker = søkerService.getSøker()
                .doOnNext {
                    log.info("Søker hentet.")
                    log.info("Validerer søker...")
                    it.validate()
                    log.info("Søker validert.")
                }
                .doOnError { throw SøknadInnsendingFeiletException("Oppslag av søker feilet.") }


        log.info("Henter legeerklæringer...")
        val legeerklæringsFiler: Flux<DocumentJsonDTO> = vedleggService.hentVedlegg(søknad.legeerklæring)
                .doOnComplete { log.info("LegeerklaringsFiler hentet.") }
                .doOnError { throw SøknadInnsendingFeiletException("Henting av legeerklæringer feilet.") }

        log.info("Henter samvarsavtaleFiler...")
        var samvarsavtaleFiler: Flux<DocumentJsonDTO>? = søknad.samværsavtale?.let {
            vedleggService.hentVedlegg(it)
        }
        if (samvarsavtaleFiler != null) {
            samvarsavtaleFiler = samvarsavtaleFiler
                    .doOnComplete { log.info("SamvarsavtaleFiler hentet.") }
                    .doOnError { throw SøknadInnsendingFeiletException("Henting av samværsavtaler feilet.") }

        }

        Flux
                .concat(legeerklæringsFiler, samvarsavtaleFiler).collectList()
                .subscribe {
                    val vedleggsUrler = mutableListOf<URL>()
                    vedleggsUrler.addAll(søknad.legeerklæring)
                    vedleggsUrler.addAll(søknad.samværsavtale!!)

                    log.info("Validerer vedleggene...")
                    it.validerVedleggene(vedleggsUrler)
                    log.info("Vedleggene validert.")
                }


        log.info("Sender søknad for mottak")
        return Mono.`when`(søker, legeerklæringsFiler, samvarsavtaleFiler)
                .publish { søknadMottakService.sendSøknad(komplettSøknadDTO = søknad.TilKomplettSøknad(søker)) }

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
