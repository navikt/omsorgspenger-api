package no.nav.omsorgspenger.wiremock

import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response
import no.nav.helse.TestUtils

class SokerResponseTransformer : ResponseTransformer() {
    override fun transform(
        request: Request?,
        response: Response?,
        files: FileSource?,
        parameters: Parameters?
    ): Response {
        return Response.Builder.like(response)
            .body(
                getResponse(
                ident = TestUtils.getIdentFromIdToken(request)
            )
            )
            .build()
    }

    override fun getName(): String {
        return "k9-oppslag-soker"
    }

    override fun applyGlobally(): Boolean {
        return false
    }

}

private fun getResponse(ident: String): String {
    when(ident) {
        "290990123456" -> {
            return """
            {
                "etternavn": "MORSEN",
                "fornavn": "MOR",
                "mellomnavn": "HEISANN",
                "aktør_id": "12345",
                "fødselsdato": "1997-05-25"
            }
        """.trimIndent()
        } "12125012345" -> {
            return """
            {
                "etternavn": "MORSEN",
                "fornavn": "MOR",
                "mellomnavn": "HEISANN",
                "aktør_id": "12345",
                "fødselsdato": "2050-12-12"
            }
        """.trimIndent()
        } else -> {
            return """
                {}
            """.trimIndent()
        }
    }
}
