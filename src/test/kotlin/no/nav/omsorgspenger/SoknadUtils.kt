package no.nav.omsorgspenger

import no.nav.omsorgspenger.soker.Søker
import java.time.LocalDate

class SoknadUtils {
    companion object {
        val søker = Søker(
            aktørId = "12345",
            fødselsdato = LocalDate.parse("2000-01-01"),
            fornavn = "Kjell",
            fødselsnummer = "26104500284"
        )

        fun forLangtNavn() =
            "DetteNavnetErForLangtDetteNavnetErForLangtDetteNavnetErForLangtDetteNavnetErForLangtDetteNavnetErForLangt"

        fun bodyMedFodselsnummerPaaBarn(
            fodselsnummer: String,
            legeerklæringUrl: String,
            samværsavtaleUrl: String,
            relasjonTilBarnet: String? = "mor"
        ): String {
            //language=JSON
            return """
                {
                  "nyVersjon": true,
                  "språk": "nb",
                  "arbeidssituasjon": ["arbeidstaker", "frilanser", "selvstendigNæringsdrivende"],
                  "kroniskEllerFunksjonshemming": true,
                  "barn": {
                    "navn": "Ole Dole Doffen",
                    "fødselsdato": "2005-02-23",
                    "fødselsnummer": "$fodselsnummer",
                    "aktørId": "123456"
                  },
                  "sammeAddresse": true,
                  "relasjonTilBarnet": "$relasjonTilBarnet",
                  "legeerklæring": [],
                  "samværsavtale": [
                    "$samværsavtaleUrl"
                  ],
                  "medlemskap": {
                    "harBoddIUtlandetSiste12Mnd": true,
                    "utenlandsoppholdSiste12Mnd": [
                      {
                        "fraOgMed": "2020-01-31",
                        "tilOgMed": "2020-02-31",
                        "landkode": "DK",
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
                  "harBekreftetOpplysninger": true
                }
            """.trimIndent()
        }

        fun bodyMedAktoerIdPaaBarn(
            aktoerId: String,
            legeerklæringUrl: String,
            samværsavtaleUrl: String,
            barnetsNorskIdentifikator: String?
        ): String {
            //language=JSON
            return """
                {
                  "nyVersjon": true,
                  "språk": "nb",
                  "arbeidssituasjon": ["arbeidstaker", "frilanser", "selvstendigNæringsdrivende"],
                  "kroniskEllerFunksjonshemming": true,
                  "barn": {
                    "fødselsdato": "2005-02-23",
                    "aktørId": "$aktoerId"
                  },
                  "sammeAddresse": true,
                  "relasjonTilBarnet": "mor",
                  "legeerklæring": [],
                  "samværsavtale": [
                    "$samværsavtaleUrl"
                  ],
                  "medlemskap": {
                    "harBoddIUtlandetSiste12Mnd": true,
                    "utenlandsoppholdSiste12Mnd": [
                      {
                        "fraOgMed": "2020-01-31",
                        "tilOgMed": "2020-02-31",
                        "landkode": "DK",
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
                  "harBekreftetOpplysninger": true
                }
            """.trimIndent()
        }


        fun bodyUtenIdPaaBarn(
            legeerklæringUrl: String,
            samværsavtaleUrl: String
        ): String {
            //language=JSON
            return """
                {
                  "nyVersjon": true,
                  "språk": "nb",
                  "arbeidssituasjon": ["arbeidstaker", "frilanser", "selvstendigNæringsdrivende"],
                  "kroniskEllerFunksjonshemming": true,
                  "barn": {
                    "fødselsdato": "2005-01-23"
                  },
                  "sammeAddresse": true,
                  "relasjonTilBarnet": "mor",
                  "legeerklæring": [],
                  "samværsavtale": [
                    "$samværsavtaleUrl"
                  ],
                  "medlemskap": {
                    "harBoddIUtlandetSiste12Mnd": true,
                    "utenlandsoppholdSiste12Mnd": [
                      {
                        "fraOgMed": "2020-01-31",
                        "tilOgMed": "2020-02-31",
                        "landkode": "DK",
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
                  "harBekreftetOpplysninger": true
                }
            """.trimIndent()
        }
    }
}
