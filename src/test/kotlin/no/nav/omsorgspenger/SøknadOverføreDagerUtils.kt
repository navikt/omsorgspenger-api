package no.nav.omsorgspenger

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.dusseldorf.ktor.jackson.dusseldorfConfigured
import no.nav.omsorgspenger.soknadOverforeDager.Arbeidssituasjon

class SøknadOverføreDagerUtils {
    companion object {

        fun fullBody(harSamfunnskritiskJobb: Boolean = true,
                     arbeidssituasjon: List<Arbeidssituasjon> = listOf(Arbeidssituasjon.SELVSTENDIGNÆRINGSDRIVENDE),
                     landkode: String = "DK"
        ): String {
            //language=JSON
            val arbeidssituasjonSomJson = jacksonObjectMapper().dusseldorfConfigured().writerWithDefaultPrettyPrinter().writeValueAsString(arbeidssituasjon)
            val landkodeSomJson = jacksonObjectMapper().dusseldorfConfigured().writerWithDefaultPrettyPrinter().writeValueAsString(landkode)

            return """
                {
                  "språk": "nb",
                  "arbeidssituasjon": $arbeidssituasjonSomJson,
                  "medlemskap": {
                    "harBoddIUtlandetSiste12Mnd": true,
                    "utenlandsoppholdSiste12Mnd": [
                      {
                        "fraOgMed": "2020-01-31",
                        "tilOgMed": "2020-02-31",
                        "landkode": $landkodeSomJson,
                        "landnavn": "Danmark"
                      }
                    ],
                    "skalBoIUtlandetNeste12Mnd": true,
                    "utenlandsoppholdNeste12Mnd": [
                      {
                        "fraOgMed": "2020-01-31",
                        "tilOgMed": "2020-02-31",
                        "landkode": "DK",
                        "landnavn": "Danmark"
                      }
                    ]
                  },
                  "harForståttRettigheterOgPlikter": true,
                  "harBekreftetOpplysninger": true,
                  "antallDager": 5,
                  "mottakerAvDager": 123456789,
                  "harSamfunnskritiskJobb": $harSamfunnskritiskJobb
                }
            """.trimIndent()
        }
    }
}
