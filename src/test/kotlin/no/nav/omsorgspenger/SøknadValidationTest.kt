package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.soknad.BarnDetaljer
import no.nav.omsorgspenger.soknad.starterMedFodselsdato
import no.nav.omsorgspenger.soknad.valider
import org.junit.Test
import java.net.URL
import kotlin.test.assertTrue

internal class SøknadValideringsTest {
    companion object {
        val gyldigSøknad = SøknadUtils.gyldigSøknad(samværsavtalURL = "http://localhost:8080/vedlegg/1")
    }

    @Test
    fun `Skal ikke feile på gyldig søknad`() {
        gyldigSøknad.valider()
    }

    @Test
    fun `Tester gyldig fødselsdato dersom dnunmer`() {
        val starterMedFodselsdato = "630293".starterMedFodselsdato()
        assertTrue(starterMedFodselsdato)
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn mangler både aktørID og norskIdentnummer 1`() {
        gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = null,
                aktørId = null
            )
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn har både aktørID og norskIdentnummer`() {
        gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = "02119970078",
                aktørId = "1234"
            )
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn norskIdentnummer er null`() {
        gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = null
            )
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn norskIdentnummer er bare whitespace`() {
        gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = "  "
            )
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom samvæsavtale er tom`() {
        gyldigSøknad.copy(
            samværsavtale = listOf()
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom samvæsavtaleURL er ugydlig format`() {
        gyldigSøknad.copy(
            samværsavtale = listOf(URL("http://localhost/FEIL/1"))
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent feil dersom harBekreftetOpplysninger er false`() {
        gyldigSøknad.copy(
            harBekreftetOpplysninger = false
        ).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Forvent feil dersom harForståttRettigheterOgPlikter er false`() {
        gyldigSøknad.copy(
            harForståttRettigheterOgPlikter = false
        ).valider()
    }


}
