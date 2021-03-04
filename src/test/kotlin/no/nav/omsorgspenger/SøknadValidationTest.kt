package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.SøknadUtils.Companion.barn
import no.nav.omsorgspenger.SøknadUtils.Companion.søker
import no.nav.omsorgspenger.k9format.tilK9Format
import no.nav.omsorgspenger.soknad.BarnDetaljer
import no.nav.omsorgspenger.soknad.starterMedFodselsdato
import no.nav.omsorgspenger.soknad.valider
import org.junit.Test
import java.net.URL
import java.time.ZonedDateTime
import kotlin.test.assertTrue

internal class SøknadValideringsTest {
    companion object {
        val gyldigSøknad = SøknadUtils.gyldigSøknad(samværsavtalURL = "http://localhost:8080/vedlegg/1")
    }

    @Test
    fun `Skal ikke feile på gyldig søknad`() {
        gyldigSøknad.valider(gyldigSøknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test
    fun `Tester gyldig fødselsdato dersom dnunmer`() {
        val starterMedFodselsdato = "630293".starterMedFodselsdato()
        assertTrue(starterMedFodselsdato)
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn mangler både aktørID og norskIdentnummer 1`() {
        val søknad = gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = null,
                aktørId = null
            )
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn har både aktørID og norskIdentnummer`() {
        val søknad = gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = "02119970078",
                aktørId = "1234"
            )
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn norskIdentnummer er null`() {
        val søknad = gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = null
            )
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom barn norskIdentnummer er bare whitespace`() {
        val søknad = gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = "  "
            )
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom samvæsavtale er tom`() {
        val søknad = gyldigSøknad.copy(
            samværsavtale = listOf()
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test(expected = Throwblem::class)
    fun `Forvent violation dersom samvæsavtaleURL er ugydlig format`() {
        val søknad = gyldigSøknad.copy(
            samværsavtale = listOf(URL("http://localhost/FEIL/1"))
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test(expected = Throwblem::class)
    fun `Forvent feil dersom harBekreftetOpplysninger er false`() {
        val søknad = gyldigSøknad.copy(
            harBekreftetOpplysninger = false
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }

    @Test(expected = Throwblem::class)
    fun `Forvent feil dersom harForståttRettigheterOgPlikter er false`() {
        val søknad = gyldigSøknad.copy(
            harForståttRettigheterOgPlikter = false
        )
        søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker, barn))
    }
}
