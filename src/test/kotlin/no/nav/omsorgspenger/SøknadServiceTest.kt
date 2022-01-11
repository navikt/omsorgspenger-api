package no.nav.omsorgspenger

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import no.nav.helse.dusseldorf.ktor.auth.IdToken
import no.nav.helse.dusseldorf.testsupport.jws.Azure
import no.nav.k9.kafka.KafkaProducer
import no.nav.k9.kafka.Metadata
import no.nav.omsorgspenger.barn.BarnService
import no.nav.omsorgspenger.general.CallId
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soknad.MeldingRegistreringFeiletException
import no.nav.omsorgspenger.soknad.SøknadService
import no.nav.omsorgspenger.vedlegg.DokumentEier
import no.nav.omsorgspenger.vedlegg.Vedlegg
import no.nav.omsorgspenger.vedlegg.VedleggService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.Test

internal class SøknadServiceTest{
    @RelaxedMockK
    lateinit var kafkaProducer: KafkaProducer

    @RelaxedMockK
    lateinit var søkerService: SøkerService

    @RelaxedMockK
    lateinit var barnService: BarnService

    @RelaxedMockK
    lateinit var vedleggService: VedleggService

    lateinit var søknadService: SøknadService

    @BeforeEach
    internal fun setUp() {
        MockKAnnotations.init(this)
        søknadService = SøknadService(
            søkerService = søkerService,
            kafkaProducer = kafkaProducer,
            vedleggService = vedleggService,
            barnService = barnService
        )

        assertNotNull(kafkaProducer)
        assertNotNull(søknadService)
    }

    @Test
    internal fun `Tester at den fjerner hold på persistert vedlegg dersom kafka feiler`() {
        assertThrows<MeldingRegistreringFeiletException> {
            runBlocking {
                coEvery {søkerService.getSoker(any(), any()) } returns Søker(
                    aktørId = "123",
                    fødselsdato = LocalDate.parse("2000-01-01"),
                    fødselsnummer = "290990123456"
                )

                coEvery {vedleggService.hentVedlegg(vedleggUrls = any(), any(), any(), any()) } returns listOf(Vedlegg("bytearray".toByteArray(), "vedlegg", "vedlegg", DokumentEier("290990123456")))

                every { kafkaProducer.produserKafkaMelding(any(), any()) } throws Exception("Mocket feil ved kafkaProducer")

                søknadService.registrer(
                    søknad = SøknadUtils.gyldigSøknad(
                        legeerklæringURL = "http://localhost:8080/vedlegg/1"
                    ),
                    metadata = Metadata(
                        version = 1,
                        correlationId = "123"
                    ),
                    idToken = IdToken(Azure.V2_0.generateJwt(clientId = "ikke-authorized-client", audience = "omsorgsdager-melding-api")),
                    callId = CallId("abc")
                )
            }
        }

        coVerify(exactly = 1) { vedleggService.fjernHoldPåPersistertVedlegg(any(), any(), any()) }
    }
}