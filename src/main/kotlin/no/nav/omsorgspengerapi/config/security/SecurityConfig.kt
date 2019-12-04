package no.nav.omsorgspengerapi.config.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class SecurityConfig(
        private val webClient: WebClient,
        private val audienceValidator: AudienceValidator
) {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    lateinit var jwkSetUri: String

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AudienceValidator::class.java)
    }

    @Bean
    internal fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
                .authorizeExchange { exchanges ->
                    exchanges
                            .pathMatchers("/actuator/**").permitAll()
                            .anyExchange().authenticated()
                }
                .oauth2ResourceServer().jwt().jwtDecoder(jwtDecoder(webClient))

        return http.build()
    }

    fun jwtDecoder(webClient: WebClient): ReactiveJwtDecoder {
        val tokenAudienceValidator: DelegatingOAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(audienceValidator)
        val nimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .webClient(webClient)
                .build()

        nimbusReactiveJwtDecoder.setJwtValidator(tokenAudienceValidator)
        return nimbusReactiveJwtDecoder
    }
}