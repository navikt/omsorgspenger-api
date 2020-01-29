package no.nav.omsorgspengerapi.config.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
        @Qualifier("tokenAuthorizationClient") private val webClient: WebClient,
        val corsProps: CorsConfigurationProperties,
        private val audienceValidator: AudienceValidator
) {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    lateinit var jwkSetUri: String

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AudienceValidator::class.java)
    }

    init {
        log.info("CORS --> allowed origins: ${corsProps.allowedOrigins}")
        log.info("CORS --> allowed methods: ${corsProps.allowedMethods}")
        log.info("CORS --> maxAge: ${corsProps.maxAge}")
    }

    @Bean
    internal fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
                .cors { cors: ServerHttpSecurity.CorsSpec ->
                    cors.configurationSource { ex: ServerWebExchange ->

                        val corsConfig = CorsConfiguration().applyPermitDefaultValues()
                        corsConfig.allowedOrigins = corsProps.allowedOrigins
                        corsConfig.exposedHeaders = listOf(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
                        corsConfig.allowCredentials = true
                        corsConfig.maxAge = corsProps.maxAge.toLong()

                        ex.response.headers.accessControlAllowOrigin = corsConfig.checkOrigin(ex.request.headers.origin)

                        corsConfig
                    }
                }
                .csrf().disable()
                .authorizeExchange { exchanges ->
                    exchanges
                            .pathMatchers("/v3/api-docs/**",
                                    "/configuration/ui",
                                    "/swagger-resources/**",
                                    "/configuration/security",
                                    "/swagger-ui.html",
                                    "/webjars/**").permitAll()
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