package no.nav.omsorgspengerapi.oppslag

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class K9OppslagClient {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9OppslagClient.javaClass)
    }

    @Bean()
    protected fun rest(): WebClient {

        return WebClient.builder()
                .defaultHeader("Accept", "application/json")
                .filter(logRequest())
                .filter(ServerBearerExchangeFilterFunction())
                .build()
    }

    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest: ClientRequest, next: ExchangeFunction ->
            log.info("Utgående kall: {} {}", clientRequest.method(), clientRequest.url())
            log.info("Headers: {}", clientRequest.headers())

            next.exchange(clientRequest)
        }
    }
}