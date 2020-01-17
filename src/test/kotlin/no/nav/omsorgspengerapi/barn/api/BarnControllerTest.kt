package no.nav.omsorgspengerapi.barn.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.core.publisher.Flux

@ExtendWith(SpringExtension::class)
@WebFluxTest(BarnController::class)
@WithMockUser()
open class BarnControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockkBean
    lateinit var barnService: BarnService

    @Test
    fun `Forvent en liste med barn, når status er OK`() {
        val forventetBarn = Barn(
                navn = "Ole Dole Doffen",
                fødselsdato = "2009-02-23",
                aktørId = "123456"
        )

        every { barnService.getBarn() } returns Flux.just(forventetBarn)

        client.get()
                .uri("/barn")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<Barn>()
                .contains(forventetBarn)

    }
}