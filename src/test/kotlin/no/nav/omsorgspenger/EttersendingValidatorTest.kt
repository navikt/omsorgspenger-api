package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.ettersending.Ettersending
import no.nav.omsorgspenger.ettersending.valider
import org.junit.Test
import java.net.URL

class EttersendingValidatorTest{

    @Test
    fun `Gyldig ettersending skal ikke gi feil`(){
        hentGyldigEttersending().valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harBekreftetOpplysninger er false`(){
        Ettersending("nb", listOf(), true, false, "Masse forklaringer", "omsorgspenger").valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harForståttRettigheterOgPlikter er false`(){
        Ettersending("nb", listOf(), false, true, "Masse forklaringer", "omsorgspenger").valider()
    }

    //TODO: Flere tester når søknaden utvides

    private fun hentGyldigEttersending() = Ettersending(
        språk = "nb",
        harBekreftetOpplysninger = true,
        harForståttRettigheterOgPlikter = true,
        beskrivelse = "Masse tekst",
        søknadstype = "omsorgspenger",
        vedlegg = listOf(
            URL("http://localhodt:8080/vedlegg/1"),
            URL("http://localhodt:8080/vedlegg/2"),
            URL("http://localhodt:8080/vedlegg/3")
        )
    )
}