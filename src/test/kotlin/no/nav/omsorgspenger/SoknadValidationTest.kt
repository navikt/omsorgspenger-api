package no.nav.omsorgspenger

import no.nav.omsorgspenger.barn.Barn
import no.nav.omsorgspenger.soknad.Medlemskap
import no.nav.omsorgspenger.soknad.SøkerBarnRelasjon
import no.nav.omsorgspenger.soknad.Søknad
import no.nav.omsorgspenger.soknad.Utenlandsopphold
import org.junit.Test
import java.net.URL
import java.time.LocalDate


internal class SøknadValideringsTest {

    @Test
    internal fun `Til dato kan ikke være før fra dato`() {
        val søknad = Søknad(
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
                navn = "Ole Dole Doffen",
                fødselsdato = LocalDate.now().minusDays(895),
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
                Utenlandsopphold(
                    LocalDate.of(2020, 1, 2),
                    LocalDate.of(2020, 1, 1),
                    "NO", "Norge")

            )
        )
       // val exception = Assertions.assertThrows(SøknadValideringException::class.java) { søknad.valider() }

//        val forventetViolation = Violation(
//            parameterType = ParameterType.ENTITY,
//            parameterName = "Utenlandsopphold[0]",
//            reason = "Til dato kan ikke være før fra dato",
//            invalidValue = "fraOgMed eller tilOgMed"
//        )
//
//        assertThat(exception.violations).contains(forventetViolation)
    }

    @Test
    internal fun `Mangler landkode`() {
        val søknad = Søknad(
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
                navn = "Ole Dole Doffen",
                fødselsdato = LocalDate.now().minusDays(895),
                aktørId = "123456"
            ),
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = false
            ),
            samværsavtale = listOf(
            ),
            legeerklæring = listOf(
            ),
            utenlandsopphold = listOf(
                Utenlandsopphold(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                    "", "Norge")

            )
        )
//        val exception = Assertions.assertThrows(SøknadValideringException::class.java) { søknad.valider() }
//
//        val forventetViolation = Violation(
//            parameterType = ParameterType.ENTITY,
//            parameterName = "Utenlandsopphold[0]",
//            reason = "Landkode er ikke satt",
//            invalidValue = "landkode"
//        )
//
//        assertThat(exception.violations).contains(forventetViolation)
    }

    @Test
    internal fun `Mangler landnavn`() {
        val søknad = Søknad(
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
                navn = "Ole Dole Doffen",
                fødselsdato = LocalDate.now().minusDays(895),
                aktørId = "123456"
            ),
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = false
            ),
            samværsavtale = listOf(
            ),
            legeerklæring = listOf(
            ),
            utenlandsopphold = listOf(
                Utenlandsopphold(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                    "NO", "")

            )
        )
//        val exception = Assertions.assertThrows(SøknadValideringException::class.java) { søknad.valider() }
//
//        val forventetViolation = Violation(
//            parameterType = ParameterType.ENTITY,
//            parameterName = "Utenlandsopphold[0]",
//            reason = "Landnavn er ikke satt",
//            invalidValue = "landnavn"
//        )
//
//        assertThat(exception.violations).contains(forventetViolation)
    }
}
