package no.nav.omsorgspengerapi.soknad.api

import no.nav.omsorgspengerapi.barn.api.Barn
import no.nav.omsorgspengerapi.soknad.mottak.Utenlandsopphold
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.LocalDate


internal class ApplicationValidatorTest {

    @Test
    internal fun `Til dato kan ikke være før fra dato`() {
        val søknad = ApplicationV1(
                newVersion = false,
                sprak = "nb",
                erYrkesaktiv = true,
                kroniskEllerFunksjonshemming = true,
                delerOmsorg = false,
                sammeAddresse = true,
                harBekreftetOpplysninger = true,
                harForstattRettigheterOgPlikter = true,
                relasjonTilBarnet = ApplicantChildRelations.FAR,
                barn = Barn(
                        navn = "Ole Dole Doffen",
                        fødselsdato = "2009-02-23",
                        aktørId = "123456"
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
                ),
                utenlandsopphold = listOf(
                        Utenlandsopphold(
                                LocalDate.of(2020, 1, 2),
                                LocalDate.of(2020, 1, 1),
                                "NO", "Norge")

                )
        )
        val exception = Assertions.assertThrows(ApplicationValidationException::class.java) { søknad.validate() }

        val forventetViolation = Violation(
                parameterType = ParameterType.ENTITY,
                parameterName = "Utenlandsopphold[0]",
                reason = "Til dato kan ikke være før fra dato",
                invalidValue = "fraOgMed eller tilOgMed"
        )

        assertThat(exception.violations).contains(forventetViolation)
    }

    @Test
    internal fun `Mangler landkode`() {
        val søknad = ApplicationV1(
                newVersion = false,
                sprak = "nb",
                erYrkesaktiv = true,
                kroniskEllerFunksjonshemming = true,
                delerOmsorg = false,
                sammeAddresse = true,
                harBekreftetOpplysninger = true,
                harForstattRettigheterOgPlikter = true,
                relasjonTilBarnet = ApplicantChildRelations.FAR,
                barn = Barn(
                        navn = "Ole Dole Doffen",
                        fødselsdato = "2009-02-23",
                        aktørId = "123456"
                ),
                medlemskap = Medlemskap(
                        harBoddIUtlandetSiste12Mnd = false,
                        skalBoIUtlandetNeste12Mnd = false
                ),
                samvarsavtale = listOf(
                ),
                legeerklaring = listOf(
                ),
                utenlandsopphold = listOf(
                        Utenlandsopphold(
                                LocalDate.of(2020, 1, 1),
                                LocalDate.of(2020, 1, 2),
                                "", "Norge")

                )
        )
        val exception = Assertions.assertThrows(ApplicationValidationException::class.java) { søknad.validate() }

        val forventetViolation = Violation(
                parameterType = ParameterType.ENTITY,
                parameterName = "Utenlandsopphold[0]",
                reason = "Landkode er ikke satt",
                invalidValue = "landkode"
        )

        assertThat(exception.violations).contains(forventetViolation)
    }

    @Test
    internal fun `Mangler landnavn`() {
        val søknad = ApplicationV1(
                newVersion = false,
                sprak = "nb",
                erYrkesaktiv = true,
                kroniskEllerFunksjonshemming = true,
                delerOmsorg = false,
                sammeAddresse = true,
                harBekreftetOpplysninger = true,
                harForstattRettigheterOgPlikter = true,
                relasjonTilBarnet = ApplicantChildRelations.FAR,
                barn = Barn(
                        navn = "Ole Dole Doffen",
                        fødselsdato = "2009-02-23",
                        aktørId = "123456"
                ),
                medlemskap = Medlemskap(
                        harBoddIUtlandetSiste12Mnd = false,
                        skalBoIUtlandetNeste12Mnd = false
                ),
                samvarsavtale = listOf(
                ),
                legeerklaring = listOf(
                ),
                utenlandsopphold = listOf(
                        Utenlandsopphold(
                                LocalDate.of(2020, 1, 1),
                                LocalDate.of(2020, 1, 2),
                                "NO", "")

                )
        )
        val exception = Assertions.assertThrows(ApplicationValidationException::class.java) { søknad.validate() }

        val forventetViolation = Violation(
                parameterType = ParameterType.ENTITY,
                parameterName = "Utenlandsopphold[0]",
                reason = "Landnavn er ikke satt",
                invalidValue = "landnavn"
        )

        assertThat(exception.violations).contains(forventetViolation)
    }
}
