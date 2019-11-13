package no.nav.omsorgspengerapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
@EnableWebFluxSecurity
class OmsorgspengerApiApplication

fun main(args: Array<String>) {
	runApplication<OmsorgspengerApiApplication>(*args)

}