package no.nav.omsorgspenger

class SøknadEttersendingUtils {
    companion object {
        fun fullBody(
            vedleggUrl1: String,
            vedleggUrl2: String
        ): String {
            //language=JSON

            return """
                {
                  "språk": "nb",
                  "vedlegg": [
                    "$vedleggUrl1",
                    "$vedleggUrl2"
                  ],
                  "harForståttRettigheterOgPlikter": true,
                  "harBekreftetOpplysninger": true,
                  "beskrivelse": "Masse tekst",
                  "søknadstype": "omsorgspenger"
                }
            """.trimIndent()
        }
    }
}
