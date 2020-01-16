package no.nav.omsorgspengerapi.config.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class CorsGlobalConfiguration(val corsConfiguration: CorsConfiguration) : WebFluxConfigurer {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CorsGlobalConfiguration::class.java)
    }

    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        log.info("CORS --> allowed origins: ${corsConfiguration.allowedOrigins}")
        log.info("CORS --> allowed methods: ${corsConfiguration.allowedMethods}")
        log.info("CORS --> maxAge: ${corsConfiguration.maxAge}")

        corsRegistry
                .addMapping(corsConfiguration.applyToPathPattern)
                .allowedOrigins(*corsConfiguration.allowedOrigins.toTypedArray())
                .allowedMethods(*corsConfiguration.allowedMethods.toTypedArray())
                .maxAge(corsConfiguration.maxAge.toLong())
    }

    @Configuration
    @ConfigurationProperties(prefix = "nav.no.security.cors")
    class CorsConfiguration {
        lateinit var allowedOrigins: List<String>
        lateinit var allowedMethods: List<String>
        lateinit var maxAge: Integer
        lateinit var applyToPathPattern: String
    }
}