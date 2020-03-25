package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.ettersending.SøknadEttersending
import no.nav.omsorgspenger.ettersending.valider
import org.junit.Test
import java.net.URL
import java.util.*

class SøknadEttersendingValidatorTest{

    @Test
    fun `Gyldig søknad skal ikke gi feil`(){
        hentGyldigSøknad().valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harBekreftetOpplysninger er false`(){
        SøknadEttersending("nb", listOf(), true, false).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harForståttRettigheterOgPlikter er false`(){
        SøknadEttersending("nb", listOf(), false, true).valider()
    }

    //TODO: Flere tester når søknaden utvides

    private fun hentGyldigSøknad() = SøknadEttersending(
        språk = "nb",
        harBekreftetOpplysninger = true,
        harForståttRettigheterOgPlikter = true,
        vedlegg = listOf(
            URL("http://localhodt:8080/vedlegg/1"),
            URL("http://localhodt:8080/vedlegg/2"),
            URL("http://localhodt:8080/vedlegg/3")
        )
    )
}