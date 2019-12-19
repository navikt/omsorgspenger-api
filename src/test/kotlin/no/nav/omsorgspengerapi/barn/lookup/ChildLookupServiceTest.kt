package no.nav.omsorgspengerapi.barn.lookup

import brave.Tracer
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.omsorgspengerapi.barn.api.ChildLookupException
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
internal class ChildLookupServiceTest {

    @Autowired
    lateinit var apiGatewayApiKey: ApiGatewayApiKey

    @MockK
    lateinit var tracer: Tracer

    @Autowired
    @Qualifier("k9LookuoClient")
    lateinit var client: WebClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var childLookupService: ChildLookupService

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
        childLookupService = ChildLookupService(client, apiGatewayApiKey, tracer)
    }

    @Test
    fun `Expect a list of childeren, upon status OK from upstream service`() {
        val expectedChild = defaultApplicant()

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/meg"))
                .withQueryParams(attributes)
                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, WireMock.equalTo(apiGatewayApiKey.key))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(bodyToJsonString(expectedChild))
                )
        )

        val actualApplicant = childLookupService.lookupChildren()

        StepVerifier.create(actualApplicant)
                .assertNext { expectedChild }
                .expectComplete()
                .verify()
    }

    @Test
    fun `Expect an childLookupException, upon status INTERNAL_SERVER_ERROR from upstream service`() {

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/meg"))

                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, WireMock.equalTo(apiGatewayApiKey.key))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
        )


        val actualError = childLookupService.lookupChildren()

        StepVerifier.create(actualError)
                .expectError(ChildLookupException::class.java)
                .verify()
    }

    @Test
    fun `Expect an childLookupException, upon status BAD_REQUEST from upstream service`() {

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/meg"))
                .withQueryParams(invalidAttributes)
                .withHeader(NavHeaders.XCorrelationId, AnythingPattern())
                .withHeader(apiGatewayApiKey.header, WireMock.equalTo(apiGatewayApiKey.key))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                )
        )

        val actualError = childLookupService.lookupChildren()

        StepVerifier.create(actualError)
                .expectError(ChildLookupException::class.java)
                .verify()
    }

    private fun defaultApplicant(
            fornavn: String = "Ole",
            mellomnavn: String = "mock",
            etternavn: String = "Nordmann",
            fodselsdato: LocalDate = LocalDate.now().minusYears(30),
            aktoerId: String = "123456")
            : ChildLookupDTO = ChildLookupDTO(
            fornavn = fornavn,
            mellomnavn = mellomnavn,
            etternavn = etternavn,
            fodselsdato = fodselsdato,
            aktoerId = aktoerId
    )

    private fun bodyToJsonString(applicant: ChildLookupDTO) = objectMapper.writeValueAsString(applicant)
}