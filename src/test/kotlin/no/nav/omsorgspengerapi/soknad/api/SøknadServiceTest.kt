package no.nav.omsorgspengerapi.soknad.api

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import no.nav.helse.soker.Søker
import no.nav.omsorgspengerapi.barn.api.Barn
import no.nav.omsorgspengerapi.soker.api.SøkerOppslagException
import no.nav.omsorgspengerapi.soker.api.SøkerService
import no.nav.omsorgspengerapi.soknad.mottak.SøknadMottakService
import no.nav.omsorgspengerapi.vedlegg.api.VedleggService
import no.nav.omsorgspengerapi.vedlegg.dokument.DocumentJsonDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.net.URL
import java.time.LocalDate
import java.util.*

@ExtendWith(SpringExtension::class)
internal class SøknadServiceTest {

    @MockK
    lateinit var søknadMottakService: SøknadMottakService

    @MockK
    lateinit var søkerService: SøkerService

    @MockK
    lateinit var vedleggService: VedleggService

    @InjectMockKs
    lateinit var søknadService: SøknadService

    @Test
    internal fun `Forvent en søknadsId, gitt at alt går bra`() {

        mockSøker()
        mockVedlegg()

        val forventetSøknadId = Mono.just(SøknadId(UUID.randomUUID().toString()))
        mockSøknadMottak(forventetSøknadId)

        val søknadId = søknadService.sendSoknad(defaultSøknad())

        StepVerifier.create(søknadId)
                .assertNext { forventetSøknadId }
                .expectComplete().log()
                .verify()
    }

    @Test
    internal fun `Forvent SøknadInnsendingFeiletException, når søker ikke er funnet`() {

        every { søkerService.getSøker() } returns Mono.error(SøkerOppslagException("Oppslag av søker feilet"))
        val forventetFeil: Mono<SøknadInnsendingFeiletException> = Mono.error(SøknadInnsendingFeiletException("Oppslag av søker feilet"))

        mockVedlegg()
        mockSøknadMottak(Mono.just(SøknadId("test")))
        val feil = søknadService.sendSoknad(defaultSøknad())

        StepVerifier.create(feil)
                .assertNext { forventetFeil }
                .expectComplete().log()
                .verify()
    }

    private fun mockSøknadMottak(forventetSøknadId: Mono<SøknadId>) {
        every { søknadMottakService.sendSøknad(capture(slot())) } returns forventetSøknadId
    }

    private fun mockSøker() {
        every { søkerService.getSøker() } returns Mono.just(defaultSøker())
    }

    private fun mockVedlegg() {
        val file = ClassPathResource("./files/spring-kotlin-59kb.png").file
        val forventetVedlegg = DocumentJsonDTO(
                content = file.readBytes(),
                contentType = MimeTypeUtils.IMAGE_PNG_VALUE,
                title = file.nameWithoutExtension
        )
        every { vedleggService.hentVedlegg(capture(slot())) } returnsMany listOf(Flux.just(forventetVedlegg), Flux.just(forventetVedlegg))
    }

    private fun defaultSøknad(): Søknad = Søknad(
            nyVersjon = true,
            språk = "nb",
            erYrkesaktiv = true,
            kroniskEllerFunksjonshemming = true,
            barn = Barn(),
            medlemskap = Medlemskap(
                    harBoddIUtlandetSiste12Mnd = false,
                    utenlandsoppholdSiste12Mnd = listOf(),
                    skalBoIUtlandetNeste12Mnd = false,
                    utenlandsoppholdNeste12Mnd = listOf()
            ),
            relasjonTilBarnet = SøkerBarnRelasjon.MOR,
            delerOmsorg = true,
            sammeAddresse = true,
            legeerklæring = listOf(URL("http://localhost:8080/vedlegg/1")),
            samværsavtale = listOf(URL("http://localhost:8080/vedlegg/2")),
            harBekreftetOpplysninger = true,
            harForståttRettigheterOgPlikter = true,
            utenlandsopphold = listOf()
    )

    private fun defaultSøker(
            fornavn: String = "Ole",
            mellomnavn: String = "mock",
            etternavn: String = "Nordmann",
            fødselsdato: LocalDate = LocalDate.now().minusYears(30),
            aktørId: String = "123456")
            : Søker = Søker(
            fornavn = fornavn,
            mellomnavn = mellomnavn,
            etternavn = etternavn,
            fødselsdato = fødselsdato,
            aktørId = aktørId
    )
}