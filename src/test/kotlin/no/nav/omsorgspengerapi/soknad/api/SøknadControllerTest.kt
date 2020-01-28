package no.nav.omsorgspengerapi.soknad.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import no.nav.omsorgspengerapi.barn.api.Barn
import no.nav.omsorgspengerapi.common.AuthUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.net.URL
import java.util.*

@ExtendWith(SpringExtension::class)
@WebFluxTest(SøknadController::class)
@WithMockUser()
internal class SøknadControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockkBean
    lateinit var søknadService: SøknadService

    @Test
    internal fun `Ved registrering av søknad, forvent status CREATED`() {
        val søknad = stubSøknad()

        every { søknadService.sendSoknad(capture(slot()), capture(slot())) } returns Mono.just(SøknadId(UUID.randomUUID().toString()))

        // https://docs.spring.io/spring-security/site/docs/current/reference/html/test-webflux.html#csrf-support
        client.mutateWith(csrf()) // Adds a valid csrf token in the request.
                .post()
                .uri("/soknad")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, AuthUtils.authToken)
                .bodyValue(søknad)
                .exchange()
                .expectStatus().isCreated
                .expectBody(SøknadId::class.java)
    }

    @Test
    internal fun `Når søknaden ikke blir validert, forvent BAD_REQUEST`() {

        val forventetViolation = Violation(
                parameterType = ParameterType.ENTITY,
                parameterName = "harForståttRettigheterOgPlikter",
                reason = "Må ha forstått rettigheter og plikter for å sende inn søknad.",
                invalidValue = null
        )
        val forventetMelding = "Søknad ikke validert."
        every { søknadService.sendSoknad(capture(slot()), capture(slot())) } throws SøknadValideringException(
                message = forventetMelding,
                violations = mutableSetOf(forventetViolation)
        )

        // https://docs.spring.io/spring-security/site/docs/current/reference/html/test-webflux.html#csrf-support
        val feilmelding = client.mutateWith(csrf()) // Adds a valid csrf token in the request.
                .post()
                .uri("/soknad")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, AuthUtils.authToken)
                .bodyValue(stubSøknad())
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(SøknadValideringException::class.java)
                .returnResult().responseBody!!

        assertThat(feilmelding).hasMessage(forventetMelding)
        assertThat(feilmelding.violations).contains(forventetViolation)
    }

    private fun stubSøknad(): Søknad {
        return Søknad(
                nyVersjon = false,
                språk = "nb",
                erYrkesaktiv = true,
                kroniskEllerFunksjonshemming = true,
                delerOmsorg = false,
                sammeAddresse = true,
                harBekreftetOpplysninger = true,
                harForståttRettigheterOgPlikter = true,
                relasjonTilBarnet = SøkerBarnRelasjon.FAR,
                barn = Barn(
                        navn = "Ole Doffen",
                        fødselsdato = "2009-02-23",
                        aktørId = "123456"
                ),
                medlemskap = Medlemskap(
                        harBoddIUtlandetSiste12Mnd = false,
                        skalBoIUtlandetNeste12Mnd = false
                ),
                samværsavtale = listOf(
                        URL("http://localhost:8080/vedlegg/1"),
                        URL("http://localhost:8080/vedlegg/2")
                ),
                legeerklæring = listOf(
                        URL("http://localhost:8080/vedlegg/3"),
                        URL("http://localhost:8080/vedlegg/4")
                ),
                utenlandsopphold = listOf(
                )
        )
    }
}
