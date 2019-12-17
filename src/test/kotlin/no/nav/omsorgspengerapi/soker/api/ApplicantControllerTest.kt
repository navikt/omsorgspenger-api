package no.nav.omsorgspengerapi.soker.api

import no.nav.helse.soker.ApplicantV1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@WebFluxTest(ApplicantController::class)
@WithMockUser()
internal class ApplicantControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockBean
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

        `when`(applicantService.getApplicant()).thenReturn(Mono.just(expectedApplicant))

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