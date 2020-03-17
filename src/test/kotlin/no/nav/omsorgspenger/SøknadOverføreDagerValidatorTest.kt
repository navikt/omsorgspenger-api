package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.soknad.*
import no.nav.omsorgspenger.soknadOverforeDager.Arbeidssituasjon
import no.nav.omsorgspenger.soknadOverforeDager.SøknadOverføreDager
import no.nav.omsorgspenger.soknadOverforeDager.valider
import org.junit.Test
import java.time.LocalDate

internal class SøknadOverføreDagerValideringsTest {

    companion object {
        private val gyldigFodselsnummerA = "26104500284"
    }

    @Test
    fun `Skal ikke feile på gyldig søknad`(){
        val søknadOverføreDager = SøknadOverføreDager(
            språk = "nb",
            antallDager = 5,
            mottakerAvDagerNorskIdentifikator = gyldigFodselsnummerA,
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = true,
                utenlandsoppholdNeste12Mnd = listOf(
                    Utenlandsopphold(
                        fraOgMed = LocalDate.now().minusDays(5),
                        tilOgMed = LocalDate.now(),
                        landkode = "NO",
                        landnavn = "Norge"
                    )
                )
            ),
            harForståttRettigheterOgPlikter = true,
            harBekreftetOpplysninger = true,
            arbeidssituasjon = listOf(
                Arbeidssituasjon.ARBEIDSTAKER
            ),
            harSamfunnskritiskJobb = true
        )
        søknadOverføreDager.valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harBekreftetOpplysninger er false`(){
        val søknadOverføreDager = SøknadOverføreDager(
            språk = "nb",
            antallDager = 5,
            mottakerAvDagerNorskIdentifikator = gyldigFodselsnummerA,
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = true,
                utenlandsoppholdNeste12Mnd = listOf(
                    Utenlandsopphold(
                        fraOgMed = LocalDate.now().minusDays(5),
                        tilOgMed = LocalDate.now(),
                        landkode = "NO",
                        landnavn = "Norge"
                    )
                )
            ),
            harForståttRettigheterOgPlikter = true,
            harBekreftetOpplysninger = false,
            arbeidssituasjon = listOf(
                Arbeidssituasjon.ARBEIDSTAKER
            ),
            harSamfunnskritiskJobb = true
        )
        søknadOverføreDager.valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harForståttRettigheterOgPlikter er false`(){
        val søknadOverføreDager = SøknadOverføreDager(
            språk = "nb",
            antallDager = 5,
            mottakerAvDagerNorskIdentifikator = gyldigFodselsnummerA,
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = true,
                utenlandsoppholdNeste12Mnd = listOf(
                    Utenlandsopphold(
                        fraOgMed = LocalDate.now().minusDays(5),
                        tilOgMed = LocalDate.now(),
                        landkode = "NO",
                        landnavn = "Norge"
                    )
                )
            ),
            harForståttRettigheterOgPlikter = false,
            harBekreftetOpplysninger = true,
            arbeidssituasjon = listOf(
                Arbeidssituasjon.ARBEIDSTAKER
            ),
            harSamfunnskritiskJobb = true
        )
        søknadOverføreDager.valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom arbeidssituasjon er tom`(){
        val søknadOverføreDager = SøknadOverføreDager(
            språk = "nb",
            antallDager = 5,
            mottakerAvDagerNorskIdentifikator = gyldigFodselsnummerA,
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = true,
                utenlandsoppholdNeste12Mnd = listOf(
                    Utenlandsopphold(
                        fraOgMed = LocalDate.now().minusDays(5),
                        tilOgMed = LocalDate.now(),
                        landkode = "NO",
                        landnavn = "Norge"
                    )
                )
            ),
            harForståttRettigheterOgPlikter = true,
            harBekreftetOpplysninger = true,
            arbeidssituasjon = listOf(),
            harSamfunnskritiskJobb = true
        )
        søknadOverføreDager.valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harSamfunnskritiskJobb er false`(){
        val søknadOverføreDager = SøknadOverføreDager(
            språk = "nb",
            antallDager = 5,
            mottakerAvDagerNorskIdentifikator = gyldigFodselsnummerA,
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = true,
                utenlandsoppholdNeste12Mnd = listOf(
                    Utenlandsopphold(
                        fraOgMed = LocalDate.now().minusDays(5),
                        tilOgMed = LocalDate.now(),
                        landkode = "NO",
                        landnavn = "Norge"
                    )
                )
            ),
            harForståttRettigheterOgPlikter = true,
            harBekreftetOpplysninger = true,
            arbeidssituasjon = listOf(
                Arbeidssituasjon.ARBEIDSTAKER
            ),
            harSamfunnskritiskJobb = false
        )
        søknadOverføreDager.valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom mottakerAvDagerNorskIdentifikator er ugyldig nr`(){
        val søknadOverføreDager = SøknadOverføreDager(
            språk = "nb",
            antallDager = 5,
            mottakerAvDagerNorskIdentifikator = "111111111",
            medlemskap = Medlemskap(
                harBoddIUtlandetSiste12Mnd = false,
                skalBoIUtlandetNeste12Mnd = true,
                utenlandsoppholdNeste12Mnd = listOf(
                    Utenlandsopphold(
                        fraOgMed = LocalDate.now().minusDays(5),
                        tilOgMed = LocalDate.now(),
                        landkode = "NO",
                        landnavn = "Norge"
                    )
                )
            ),
            harForståttRettigheterOgPlikter = true,
            harBekreftetOpplysninger = true,
            arbeidssituasjon = listOf(
                Arbeidssituasjon.ARBEIDSTAKER
            ),
            harSamfunnskritiskJobb = true
        )
        søknadOverføreDager.valider()
    }
}
