package no.nav.omsorgspengerapi.config.general.webClient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient

@Configuration
class TokenAuthorizationWebClientConfig(private val webClientConfig: WebClientConfig) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TokenAuthorizationWebClientConfig::class.java)
    }

    @Bean("tokenAuthorizationClient")
    protected fun tokenAuthorizationClient(): WebClient {
        // Configure clientConnector
        val reactorClientHttpConnector = ReactorClientHttpConnector(HttpClient.create()
                .tcpConfiguration { tcpClient: TcpClient ->
                    webClientConfig.resolveProxySettings(tcpClient) // Resolve proxy settings.
                })

        return WebClient.builder()
                .clientConnector(reactorClientHttpConnector)
                .defaultHeader("Accept", "application/json")
                .filter(ServerBearerExchangeFilterFunction())
                .build()
    }
}