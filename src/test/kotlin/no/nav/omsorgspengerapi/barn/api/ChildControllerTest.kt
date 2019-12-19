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
@WebFluxTest(ChildController::class)
@WithMockUser()
open class ChildControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockkBean
    lateinit var childService: ChildService

    @Test
    fun `Expect list of children when status OK`() {
        val expectedChild = ChildV1(
                navn = "Ole Dole Doffen",
                fodselsdato = "2009-02-23",
                aktoerId = "123456"
        )

        every { childService.getChildren() } returns Flux.just(expectedChild)

        client.get()
                .uri("/barn")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<ChildV1>()
                .contains(expectedChild)

    }
}