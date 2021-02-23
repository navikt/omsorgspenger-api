package no.nav.omsorgspenger

class SoknadUtils {
    companion object {
        fun forLangtNavn() =
            "DetteNavnetErForLangtDetteNavnetErForLangtDetteNavnetErForLangtDetteNavnetErForLangtDetteNavnetErForLangt"

        //TODO 23.02.2021 - Burde heller lage en standard gydlig søknad også bruke copy for å endre til spesielle tilfeller.
        // Bruke en .somJson metode for å mappe fra objekt til json

        fun gyldigSøknadJson(
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
                  "kroniskEllerFunksjonshemming": true,
                  "barn": {
                    "navn": "Ole Dole Doffen",
                    "norskIdentifikator": "$fodselsnummer"
                  },
                  "sammeAddresse": true,
                  "relasjonTilBarnet": "$relasjonTilBarnet",
                  "legeerklæring": [],
                  "samværsavtale": [
                    "$samværsavtaleUrl"
                  ],
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
