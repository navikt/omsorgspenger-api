package no.nav.omsorgspengerapi.config.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "nav.no.security.cors")
class CorsConfigurationProperties {
    lateinit var allowedOrigins: List<String>
    lateinit var allowedMethods: List<String>
    lateinit var maxAge: Integer
    lateinit var applyToPathPattern: String
}