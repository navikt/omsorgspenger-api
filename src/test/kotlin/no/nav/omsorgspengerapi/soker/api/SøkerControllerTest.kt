package no.nav.omsorgspengerapi.soker.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.nav.helse.soker.Søker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@WebFluxTest(SøkerController::class)
@WithMockUser()
internal class SøkerControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockkBean
    lateinit var søkerService: SøkerService

    @Test
    internal fun `Forvent en søker, når status er OK`() {
        val forventetSøker = Søker(
                fornavn = "Bjarne",
                mellomnavn = "Dahl",
                etternavn = "Moen",
                fødselsdato = LocalDate.now().minusYears(20),
                aktørId = "123456"
        )

        every { søkerService.getSøker() } returns Mono.just(forventetSøker)

        val søker = client.get()
                .uri("/soker")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(Søker::class.java)
                .returnResult().responseBody

        assertThat(søker)
                .isNotNull
                .isEqualTo(forventetSøker)
    }
}