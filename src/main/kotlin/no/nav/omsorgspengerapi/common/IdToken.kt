package no.nav.omsorgspengerapi.common

import com.nimbusds.jwt.JWTParser

data class IdToken(val value: String) {
    private val jwt = try {
        JWTParser.parse(value)
    } catch (cause: Throwable) {
        throw IdTokenInvalidFormatException(this, cause)
    }

    internal fun getId() : String? = jwt.jwtClaimsSet.getStringClaim("jti")
    internal fun getSubject() : String? = jwt.jwtClaimsSet.getStringClaim("sub")
}