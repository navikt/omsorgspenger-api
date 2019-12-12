package no.nav.omsorgspengerapi.config.general.webClient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Configuration
class K9DokumentWebClientConfig {

    @Value("\${nav.no.gateways.k9_dokument_url}")
    lateinit var baseUrl: URI

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9LookupWebClientConfig::class.java)
    }

    @Bean("k9DocumentClient")
    protected fun k9LookuoClient(): WebClient {
        return WebClient.builder()
                .baseUrl(baseUrl.toString())
                .filter(logOutgoingRequest(log))
                .filter(ServerBearerExchangeFilterFunction())
                .build()
    }
}