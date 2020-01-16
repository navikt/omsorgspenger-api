package no.nav.omsorgspengerapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.omsorgspengerapi.common.OmsorgspengerAPIErrorErrorAttributes
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import java.net.URI


@Configuration
class WebFluxConfiguration(private val objectMapper: ObjectMapper): WebFluxConfigurer {

    @Value("\${spring.codec.max-in-memory-size}")
    lateinit var maxInMemoryLimit: Integer

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(
                Jackson2JsonEncoder(objectMapper)
        )
        configurer.defaultCodecs().jackson2JsonDecoder(
                Jackson2JsonDecoder(objectMapper)
        )
        configurer.defaultCodecs().maxInMemorySize(maxInMemoryLimit.toInt())
    }

    @Bean
    fun errorAttributes(): ErrorAttributes {
        return OmsorgspengerAPIErrorErrorAttributes()
    }
}