package no.nav.omsorgspengerapi.soker.lookup

import brave.Tracer
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.omsorgspengerapi.common.NavHeaders
import no.nav.omsorgspengerapi.config.security.ApiGatewayApiKey
import no.nav.omsorgspengerapi.redis.RedisMockUtil
import no.nav.omsorgspengerapi.soker.api.SøkerOppslagException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.*
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
internal class SøkerOppslagsServiceTest {

    @Autowired
    lateinit var apiGatewayApiKey: ApiGatewayApiKey

    @MockK
    lateinit var tracer: Tracer

    @Autowired
    @Qualifier("k9LookuoClient")
    lateinit var client: WebClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var søkerOppslagsService: SøkerOppslagsService

    private val traceId = UUID.randomUUID().toString()

    companion object {
        private val attributes = mutableMapOf<String, StringValuePattern>(
                "a" to equalTo("aktør_id"),
                "a" to equalTo("fornavn"),
                "a" to equalTo("mellomnavn"),
                "a" to equalTo("etternavn"),
                "a" to equalTo("fødselsdato")
        )

        private val invalidAttributes = mutableMapOf<String, StringValuePattern>(
                "a" to notMatching("invalid"),
                "a" to notMatching("non_existens")
        )
    }

    @BeforeEach
    internal fun setUp() {
        every { tracer.currentSpan().context().traceIdString() } returns traceId
        søkerOppslagsService = SøkerOppslagsService(client, apiGatewayApiKey, tracer)
    }

    @Test
    fun `Forvent en søker når oppslagsstatus er OK`() {
        val forventetSøker = defaultOppslagsSøker()

        stubFor(get(urlPathEqualTo("/meg"))
                .withQueryParams(attributes)
                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, equalTo(apiGatewayApiKey.key))
                .willReturn(
                        aResponse()
                                .withStatus(OK.value())
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(bodyToJsonString(forventetSøker))
                )
        )

        val søker = søkerOppslagsService.slåOppSøker()

        StepVerifier.create(søker)
                .assertNext { forventetSøker }
                .expectComplete()
                .verify()
    }

    @Test
    fun `Forvent en søkerOppslagException, når oppslag av søker gir INTERNAL_SERVER_ERROR`() {

        stubFor(get(urlPathEqualTo("/meg"))

                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, equalTo(apiGatewayApiKey.key))
                .willReturn(
                        aResponse()
                                .withStatus(INTERNAL_SERVER_ERROR.value())
                )
        )


        val søkerOppslagExcception = søkerOppslagsService.slåOppSøker()

        StepVerifier.create(søkerOppslagExcception)
                .expectError(SøkerOppslagException::class.java)
                .verify()
    }

    @Test
    fun `Forvent en søkerOppslagException, når oppslag av søker gir BAD_REQUEST`() {

        stubFor(get(urlPathEqualTo("/meg"))
                .withQueryParams(invalidAttributes)
                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, equalTo(apiGatewayApiKey.key))
                .willReturn(
                        aResponse()
                                .withStatus(BAD_REQUEST.value())
                )
        )

        val actualError = søkerOppslagsService.slåOppSøker()

        StepVerifier.create(actualError)
                .expectError(SøkerOppslagException::class.java)
                .verify()
    }

    private fun defaultOppslagsSøker(
            fornavn: String = "Ole",
            mellomnavn: String = "mock",
            etternavn: String = "Nordmann",
            fødselsdato: LocalDate = LocalDate.now().minusYears(30),
            aktørId: String = "123456")
            : SøkerOppslagsDTO = SøkerOppslagsDTO(
            fornavn = fornavn,
            mellomnavn = mellomnavn,
            etternavn = etternavn,
            fødselsdato = fødselsdato,
            aktørId = aktørId
    )

    private fun bodyToJsonString(søker: SøkerOppslagsDTO) = objectMapper.writeValueAsString(søker)
}