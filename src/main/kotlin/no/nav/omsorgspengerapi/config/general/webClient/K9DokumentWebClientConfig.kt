package no.nav.omsorgspengerapi.config.general.webClient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Configuration
class K9DokumentWebClientConfig {

    @Value("\${nav.no.gateways.k9_dokument_url}")
    lateinit var baseUrl: URI

    private val inMemoryBufferSize: Int? = 8 * 1024 * 1024 // Defaults to 8MB

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9LookupWebClientConfig::class.java)
    }

    @Bean("k9DokumentKlient")
    protected fun k9LookuoClient(): WebClient {
        return WebClient.builder()
                .baseUrl(baseUrl.toString())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs { codecs ->
                            codecs.defaultCodecs().maxInMemorySize(inMemoryBufferSize!!)
                        }
                        .build()
                )
                .filter(ServerBearerExchangeFilterFunction())
                .filter(logOutgoingRequest(log))
                .build()
    }
}