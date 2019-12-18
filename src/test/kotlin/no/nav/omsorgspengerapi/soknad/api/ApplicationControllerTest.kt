package no.nav.omsorgspengerapi.soknad.api

import no.nav.omsorgspengerapi.any
import no.nav.omsorgspengerapi.barn.api.ChildV1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.net.URL

@ExtendWith(MockitoExtension::class)
@WebFluxTest(ApplicationController::class)
@WithMockUser()
internal class ApplicationControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockBean
    lateinit var applicationService: ApplicationService

    @Test
    internal fun `When registering application expect status 200`() {
        val application = stubApplicationV1()

        `when`(applicationService.sendSoknad(application)).thenReturn(Mono.just(Unit))

        // https://docs.spring.io/spring-security/site/docs/current/reference/html/test-webflux.html#csrf-support
        client.mutateWith(csrf()) // Adds a valid csrf token in the request.
                .post()
                .uri("/soknad")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(application)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .isEmpty
    }

    @Test
    internal fun `When registering an invalid application, expect bad request`() {

        val expectedViolation = Violation(
                parameterType = ParameterType.ENTITY,
                parameterName = "harForstattRettigheterOgPlikter",
                reason = "Må ha forstått rettigheter og plikter for å sende inn søknad.",
                invalidValue = null
        )
        val errorMessage = "Failed to validate received application."
        `when`(applicationService.sendSoknad(any(ApplicationV1::class.java)))
                .thenThrow(ApplicationValidationException(
                        message = errorMessage,
                        violations = mutableSetOf(expectedViolation)
                ))

        // https://docs.spring.io/spring-security/site/docs/current/reference/html/test-webflux.html#csrf-support
        val actualError = client.mutateWith(csrf()) // Adds a valid csrf token in the request.
                .post()
                .uri("/soknad")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stubApplicationV1())
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(ApplicationValidationException::class.java)
                .returnResult().responseBody!!

        assertThat(actualError).hasMessage(errorMessage)
        assertThat(actualError.violations).contains(expectedViolation)
    }

    private fun stubApplicationV1(): ApplicationV1 {
        return ApplicationV1(
                newVersion = false,
                sprak = "nb",
                erYrkesaktiv = true,
                kroniskEllerFunksjonshemming = true,
                delerOmsorg = false,
                sammeAddresse = true,
                harBekreftetOpplysninger = true,
                harForstattRettigheterOgPlikter = true,
                relasjonTilBarnet = ApplicantChildRelations.FAR,
                barn = ChildV1(
                        navn = "Ole Dole Doffen",
                        fodselsdato = "2009-02-23",
                        aktoerId = "123456"
                ),
                medlemskap = Medlemskap(
                        harBoddIUtlandetSiste12Mnd = false,
                        skalBoIUtlandetNeste12Mnd = false
                ),
                samvarsavtale = listOf(
                        URL("http://localhost:8080/vedlegg/1"),
                        URL("http://localhost:8080/vedlegg/2")
                ),
                legeerklaring = listOf(
                        URL("http://localhost:8080/vedlegg/3"),
                        URL("http://localhost:8080/vedlegg/4")
                )
        )
    }
}
