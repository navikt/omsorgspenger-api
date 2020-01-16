package no.nav.omsorgspengerapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity

@SpringBootApplication
@EnableWebFluxSecurity
@EnableConfigurationProperties
class OmsorgspengerApiApplication

fun main(args: Array<String>) {
	runApplication<OmsorgspengerApiApplication>(*args)
}