package no.nav.omsorgspenger

import no.nav.helse.dusseldorf.ktor.core.Throwblem
import no.nav.omsorgspenger.soknad.Medlemskap
import no.nav.omsorgspenger.soknadEttersending.SøknadEttersending
import no.nav.omsorgspenger.soknadEttersending.Søknadstype
import no.nav.omsorgspenger.soknadEttersending.valider
import org.junit.Test
import java.net.URL

class SøknadEttersendingValidatorTest{

    @Test
    fun `Gyldig søknad skal ikke gi feil`(){
        hentGyldigSøknad().valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harBekreftetOpplysninger er false`(){
        SøknadEttersending("nb", listOf(), true, false,
            "Masse forklaringer", Søknadstype.OMSORGSPENGER, Medlemskap(false, listOf(), false, listOf())).valider()
    }

    @Test(expected = Throwblem::class)
    fun `Skal feile dersom harForståttRettigheterOgPlikter er false`(){
        SøknadEttersending("nb", listOf(), false, true,
            "Masse forklaringer", Søknadstype.OMSORGSPENGER, Medlemskap(false, listOf(), false, listOf())).valider()
    }

    //TODO: Flere tester når søknaden utvides

    private fun hentGyldigSøknad() = SøknadEttersending(
        språk = "nb",
        harBekreftetOpplysninger = true,
        harForståttRettigheterOgPlikter = true,
        beskrivelse = "Masse tekst",
        søknadstype = Søknadstype.OMSORGSPENGER,
        vedlegg = listOf(
            URL("http://localhodt:8080/vedlegg/1"),
            URL("http://localhodt:8080/vedlegg/2"),
            URL("http://localhodt:8080/vedlegg/3")
        ),
        medlemskap = Medlemskap(
            harBoddIUtlandetSiste12Mnd = false,
            skalBoIUtlandetNeste12Mnd = false
        )
    )
}