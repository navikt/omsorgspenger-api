package no.nav.omsorgspenger

class EttersendingUtils {
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
                  "søknadstype": "omsorgspenger",
                  "medlemskap": {
                    "harBoddIUtlandetSiste12Mnd": false,
                    "utenlandsoppholdSiste12Mnd": [],
                    "skalBoIUtlandetNeste12Mnd": false,
                    "utenlandsoppholdNeste12Mnd": []
                  }
                }
            """.trimIndent()
        }
    }
}
