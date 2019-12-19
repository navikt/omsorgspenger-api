package no.nav.omsorgspengerapi.soker.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.nav.helse.soker.ApplicantV1
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
@WebFluxTest(ApplicantController::class)
@WithMockUser()
internal class ApplicantControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockkBean
    lateinit var applicantService: ApplicantService

    @Test
    internal fun `Expect an applicant when status OK`() {
        val expectedApplicant = ApplicantV1(
                fornavn = "Bjarne",
                mellomnavn = "Dahl",
                etternavn = "Moen",
                fodselsdato = LocalDate.now().minusYears(20),
                aktoer_id = "123456"
        )

        every { applicantService.getApplicant() } returns Mono.just(expectedApplicant)

        val actualApplicant = client.get()
                .uri("/soker")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(ApplicantV1::class.java)
                .returnResult().responseBody

        assertThat(actualApplicant)
                .isNotNull
                .isEqualTo(expectedApplicant)
    }
}