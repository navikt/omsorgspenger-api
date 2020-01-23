package no.nav.omsorgspengerapi.barn.lookup

import brave.Tracer
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.omsorgspengerapi.barn.api.BarnOppslagFeiletException
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.LocalDate
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(SpringExtension::class)
@AutoConfigureWireMock(port = 8090)
@ActiveProfiles("test")
internal class BarnOppslagsServiceTest {

    @Autowired
    lateinit var apiGatewayApiKey: ApiGatewayApiKey

    @MockK
    lateinit var tracer: Tracer

    @Autowired
    @Qualifier("k9LookuoClient")
    lateinit var client: WebClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var barnOppslagsService: BarnOppslagsService

    private val traceId = UUID.randomUUID().toString()

    companion object {
        private val attributes = mutableMapOf<String, StringValuePattern>(
                "a" to WireMock.equalTo("barn[].aktør_id"),
                "a" to WireMock.equalTo("barn[].fornavn"),
                "a" to WireMock.equalTo("barn[].mellomnavn"),
                "a" to WireMock.equalTo("barn[].etternavn"),
                "a" to WireMock.equalTo("barn[].fødselsdato")
        )

        private val invalidAttributes = mutableMapOf<String, StringValuePattern>(
                "a" to WireMock.notMatching("invalid"),
                "a" to WireMock.notMatching("non_existens")
        )
    }

    @BeforeEach
    internal fun setUp() {
        every { tracer.currentSpan().context().traceIdString() } returns traceId
        barnOppslagsService = BarnOppslagsService(client, apiGatewayApiKey, tracer)
    }

    @Test
    fun `Forvent en liste med barn, når oppslagsstatus er OK`() {
        val forventetBarn = defaultBarn()

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/meg"))
                .withQueryParams(attributes)
                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, WireMock.equalTo(apiGatewayApiKey.key))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(bodyToJsonString(forventetBarn))
                )
        )

        val barn = barnOppslagsService.slåOppBarn()

        StepVerifier.create(barn)
                .assertNext { forventetBarn.barn }
                .expectComplete()
                .verify()

        verify(1, getRequestedFor(urlPathEqualTo("/meg")))
    }

    @Test
    fun `Forvent en BarnOppslagException, når oppslagsstatus er INTERNAL_SERVER_ERROR`() {

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/meg"))

                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, WireMock.equalTo(apiGatewayApiKey.key))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
        )


        val feil = barnOppslagsService.slåOppBarn()

        StepVerifier.create(feil)
                .expectError(BarnOppslagFeiletException::class.java)
                .verify()
    }

    @Test
    fun `Forvent en BarnOppslagException, når oppslagsstatus er BAD_REQUEST`() {

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/meg"))
                .withQueryParams(invalidAttributes)
                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, WireMock.equalTo(apiGatewayApiKey.key))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                )
        )

        val feil = barnOppslagsService.slåOppBarn()

        StepVerifier.create(feil)
                .expectError(BarnOppslagFeiletException::class.java)
                .verify()
    }

    private fun defaultBarn(
            fornavn: String = "Ole",
            mellomnavn: String = "mock",
            etternavn: String = "Nordmann",
            fodselsdato: LocalDate = LocalDate.now().minusYears(30),
            aktoerId: String = "123456")
            : BarnOppslagRespons = BarnOppslagRespons(
            listOf(BarnOppslagDTO(
                    fornavn = fornavn,
                    mellomnavn = mellomnavn,
                    etternavn = etternavn,
                    fødselsdato = fodselsdato,
                    aktørId = aktoerId
            ))
    )

    private fun bodyToJsonString(barnOppslagRespons: BarnOppslagRespons) = objectMapper.writeValueAsString(barnOppslagRespons)
}