package no.nav.omsorgspenger

class EttersendingUtils {
    companion object {
        fun fullBody(
            vedleggUrl1: String,
            vedleggUrl2: String,
            beskrivelse : String = "Masse tekst",
            søknadstype: String = "omsorgspenger"
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
                  "beskrivelse": "$beskrivelse",
                  "søknadstype": "$søknadstype"
                }
            """.trimIndent()
        }
    }
}
