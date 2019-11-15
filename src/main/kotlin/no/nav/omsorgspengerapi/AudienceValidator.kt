package no.nav.omsorgspengerapi

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt

@Configuration
class AudienceValidator : OAuth2TokenValidator<Jwt> {

    @Value("\${nav.no.security.login-service-client-id}")
    lateinit var clientId: String

    companion object {
        private val log: org.slf4j.Logger = LoggerFactory.getLogger(AudienceValidator::class.java)
    }

    internal var error = OAuth2Error("invalid_token", "The required audience is missing", null)

    override fun validate(jwt: Jwt): OAuth2TokenValidatorResult {
        return if (jwt.audience.contains(clientId)) {
            OAuth2TokenValidatorResult.success()
        } else {
            OAuth2TokenValidatorResult.failure(error)
        }
    }
}