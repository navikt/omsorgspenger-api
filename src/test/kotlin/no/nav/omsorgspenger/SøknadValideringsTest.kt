package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.SøknadUtils.Companion.søker
import no.nav.omsorgspenger.k9format.tilK9Format
import no.nav.omsorgspenger.soknad.BarnDetaljer
import no.nav.omsorgspenger.soknad.starterMedFodselsdato
import no.nav.omsorgspenger.soknad.valider
import org.junit.jupiter.api.Assertions
import java.net.URL
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertTrue

internal class SøknadValideringsTest {
    companion object {
        val gyldigSøknad = SøknadUtils.gyldigSøknad(samværsavtalURL = "http://localhost:8080/vedlegg/1")
    }

    @Test
    fun `Skal ikke feile på gyldig søknad`() {
        gyldigSøknad.valider(gyldigSøknad.tilK9Format(ZonedDateTime.now(), søker))
    }

    @Test
    fun `Tester gyldig fødselsdato dersom dnunmer`() {
        val starterMedFodselsdato = "630293".starterMedFodselsdato()
        assertTrue(starterMedFodselsdato)
    }

    @Test
    fun `Forvent violation dersom barn mangler både aktørID og norskIdentnummer 1`() {
        val søknad = gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = null,
                aktørId = null
            )
        )
        Assertions.assertThrows(Throwblem::class.java) {
            søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker))
        }
    }

    @Test
    fun `Forvent violation dersom barn norskIdentnummer er null`() {
        val søknad = gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = null
            )
        )
        Assertions.assertThrows(Throwblem::class.java) {
            søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker))
        }
    }

    @Test
    fun `Forvent violation dersom barn norskIdentnummer er bare whitespace`() {
        val søknad = gyldigSøknad.copy(
            barn = BarnDetaljer(
                navn = "Ole Dole Doffen",
                norskIdentifikator = "  "
            )
        )
        Assertions.assertThrows(Throwblem::class.java) {
            søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker))
        }
    }

    @Test
    fun `Forvent violation dersom samvæsavtale er tom`() {
        val søknad = gyldigSøknad.copy(
            samværsavtale = listOf()
        )
        Assertions.assertThrows(Throwblem::class.java) {
            søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker))
        }
    }

    @Test
    fun `Forvent violation dersom samvæsavtaleURL er ugydlig format`() {
        val søknad = gyldigSøknad.copy(
            samværsavtale = listOf(URL("http://localhost/FEIL/1"))
        )
        Assertions.assertThrows(Throwblem::class.java) {
            søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker))
        }
    }

    @Test
    fun `Forvent feil dersom harBekreftetOpplysninger er false`() {
        val søknad = gyldigSøknad.copy(
            harBekreftetOpplysninger = false
        )
        Assertions.assertThrows(Throwblem::class.java) {
            søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker))
        }
    }

    @Test
    fun `Forvent feil dersom harForståttRettigheterOgPlikter er false`() {
        val søknad = gyldigSøknad.copy(
            harForståttRettigheterOgPlikter = false
        )
        Assertions.assertThrows(Throwblem::class.java) {
            søknad.valider(søknad.tilK9Format(ZonedDateTime.now(), søker))
        }
    }
}
