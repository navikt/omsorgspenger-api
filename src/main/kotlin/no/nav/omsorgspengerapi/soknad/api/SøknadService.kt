package no.nav.omsorgspengerapi.soknad.api

import no.nav.helse.soker.Søker
import no.nav.helse.soker.validate
import no.nav.omsorgspengerapi.soker.api.SøkerService
import no.nav.omsorgspengerapi.soknad.mottak.KomplettSøknadDTO
import no.nav.omsorgspengerapi.soknad.mottak.SøknadMottakService
import no.nav.omsorgspengerapi.vedlegg.api.VedleggJson
import no.nav.omsorgspengerapi.vedlegg.api.VedleggService
import no.nav.omsorgspengerapi.vedlegg.dokument.DocumentJsonDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple3
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
        val søkerRequest = søkerService.getSøker()
                .doOnError { throw SøknadInnsendingFeiletException("Oppslag av søker feilet.") }


        log.info("Henter legeerklæringer...")
        val legeerklæringRequest: Flux<DocumentJsonDTO> = vedleggService.hentVedlegg(søknad.legeerklæring)
                .doOnError { throw SøknadInnsendingFeiletException("Henting av legeerklæringer feilet.") }

        log.info("Henter samvarsavtaleFiler...")
        val samvarsavtaleRequest: Flux<DocumentJsonDTO> = vedleggService.hentVedlegg(søknad.samværsavtale ?: listOf())
                .doOnError { throw SøknadInnsendingFeiletException("Henting av samværsavtaler feilet.") }

        return Mono.zip(søkerRequest, legeerklæringRequest.collectList(), samvarsavtaleRequest.collectList())
                .flatMap { tuple3: Tuple3<Søker, List<DocumentJsonDTO>, List<DocumentJsonDTO>> ->
                    val søker: Søker = tuple3.t1
                    validerSøker(søker)

                    val legeerklæring = tuple3.t2
                    log.info("LegeerklaringsFiler hentet.")
                    val samværsavtale = tuple3.t3
                    log.info("SamvarsavtaleFiler hentet.")

                    validerVedleggene(søknad, legeerklæring, samværsavtale)

                    log.info("Sender søknad for mottak")
                    søknadMottakService.sendSøknad(komplettSøknadDTO = søknad.TilKomplettSøknad(
                            søker = søker,
                            legeerklæringer = legeerklæring.tilVedleggsFormat(),
                            samværsavtaler = samværsavtale.tilVedleggsFormat())
                    )
                }
    }

    private fun validerVedleggene(søknad: Søknad, legeerklæring: List<DocumentJsonDTO>, samværsavtale: List<DocumentJsonDTO>) {
        val vedleggsDokumenter = mutableListOf<DocumentJsonDTO>()
        vedleggsDokumenter.addAll(legeerklæring)
        vedleggsDokumenter.addAll(samværsavtale)

        val vedleggsUrler = mutableListOf<URL>()
        vedleggsUrler.addAll(søknad.legeerklæring)
        vedleggsUrler.addAll(søknad.samværsavtale!!)

        log.info("Validerer vedleggene...")
        vedleggsDokumenter.validerVedleggene(vedleggsUrler)
        log.info("Vedleggene validert.")
    }

    private fun validerSøker(søker: Søker) {
        log.info("Søker hentet.")
        log.info("Validerer søker...")
        søker.validate()
        log.info("Søker validert.")
    }
}

private fun List<DocumentJsonDTO>.tilVedleggsFormat(): List<VedleggJson> = map {
    VedleggJson(
            contentType = it.contentType,
            title = it.title,
            content = it.content
    )
}

private fun Søknad.TilKomplettSøknad(søker: Søker, legeerklæringer: List<VedleggJson>, samværsavtaler: List<VedleggJson>?): KomplettSøknadDTO = KomplettSøknadDTO(
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
        legeerklæring = legeerklæringer,
        samværsavtale = samværsavtaler,
        harBekreftetOpplysninger = harBekreftetOpplysninger,
        harForståttRettigheterOgPlikter = harForståttRettigheterOgPlikter
)