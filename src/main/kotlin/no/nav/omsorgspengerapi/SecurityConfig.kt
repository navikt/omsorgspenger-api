package no.nav.omsorgspengerapi

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
class SecurityConfig {

    @Bean
    internal fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
                .authorizeExchange { exchanges ->
                    exchanges
                            .pathMatchers("/actuator/**").permitAll()
                            .anyExchange().authenticated()
                }
                .oauth2ResourceServer { it.jwt() }
        return http.build()
    }

}