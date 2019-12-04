package no.nav.omsorgspengerapi.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class ApiGatewayApiKey(
        @Value("\${nav.no.authorization.api-gw-header}") val header: String,
        @Value("\${nav.no.authorization.api-gw-key}") val key: String
)