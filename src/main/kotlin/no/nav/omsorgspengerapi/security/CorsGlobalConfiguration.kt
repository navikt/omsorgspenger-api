package no.nav.omsorgspengerapi.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class CorsGlobalConfiguration : WebFluxConfigurer {

    @Value("\${nav.no.cors-addresses}")
    private lateinit var corsAddresses: List<String>

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CorsGlobalConfiguration.javaClass)
    }

    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        log.info("CORS --> allowed origins: ${corsAddresses}")

        corsRegistry
                .addMapping("/**")
                .allowedOrigins(*corsAddresses.toTypedArray())
                .allowedMethods("*")
                .maxAge(3600)
    }
}