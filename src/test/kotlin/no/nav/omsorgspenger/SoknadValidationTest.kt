package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.soknad.*
import org.junit.Test
import java.net.URL
import java.time.LocalDate
import kotlin.test.assertTrue


internal class SøknadValideringsTest {

    companion object {
        private val gyldigFodselsnummerA = "02119970078"
        private val gyldigFodselsnummerB = "19066672169"
        private val gyldigFodselsnummerC = "20037473937"
        private val dNummerA = "55125314561"
    }

    //TODO 23.02.2021 - Burde flytte flere valideringstester inn her, i stedet for applicationTest

    @Test
    fun `Tester gyldig fødselsdato dersom dnunmer`() {
        val starterMedFodselsdato = "630293".starterMedFodselsdato()
        assertTrue(starterMedFodselsdato)
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn mangler både aktørID og norskIdentnummer`() {
        Søknad(
            nyVersjon = false,
            språk = "nb",
            kroniskEllerFunksjonshemming = true,
            sammeAdresse = true,
            harBekreftetOpplysninger = true,
            harForståttRettigheterOgPlikter = true,
            relasjonTilBarnet = SøkerBarnRelasjon.FAR,
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = null,
                aktørId = null
            ),
            samværsavtale = null,
            /*
            legeerklæring = listOf(
                URL("http://localhodt:8080/vedlegg/1")
            )
            */
            legeerklæring = emptyList()
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn har både aktørID og norskIdentnummer`() {
        Søknad(
            nyVersjon = false,
            språk = "nb",
            kroniskEllerFunksjonshemming = true,
            sammeAdresse = true,
            harBekreftetOpplysninger = true,
            harForståttRettigheterOgPlikter = true,
            relasjonTilBarnet = SøkerBarnRelasjon.FAR,
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = "02119970078",
                aktørId = "1234"
            ),
            samværsavtale = null,
            /*
            legeerklæring = listOf(
                URL("http://localhodt:8080/vedlegg/1")
            )
            */
            legeerklæring = emptyList()
        ).valider()
    }
}
