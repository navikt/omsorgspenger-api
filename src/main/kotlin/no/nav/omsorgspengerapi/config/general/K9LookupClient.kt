package no.nav.omsorgspengerapi.config.general

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.ProxyProvider
import reactor.netty.tcp.TcpClient


@Configuration
class K9LookupClient {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9LookupClient.javaClass)
    }

    @Bean
    fun httpClient(proxyConfig: HttpProxyConfig): HttpClient {
        return HttpClient.create()
                .tcpConfiguration { tcpClient: TcpClient ->
                    tcpClient.proxy { proxy: ProxyProvider.TypeSpec ->
                        proxy.type(ProxyProvider.Proxy.HTTP)
                                .host(proxyConfig.httpProxyHost)
                                .port(proxyConfig.httpProxyPort.toInt())
                                .nonProxyHosts(proxyConfig.httpNonProxyHosts)
                    }
                }
    }

    @Bean
    fun reactorClientHttpConnector(httpClient: HttpClient): ReactorClientHttpConnector? {
        return ReactorClientHttpConnector(httpClient)
    }

    @Bean()
    protected fun webClient(reactorClientHttpConnector: ReactorClientHttpConnector): WebClient {

        return WebClient.builder()
                .clientConnector(reactorClientHttpConnector)
                .defaultHeader("Accept", "application/json")
                .filter(logRequest())
                .filter(ServerBearerExchangeFilterFunction())
                .build()
    }

    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest: ClientRequest, next: ExchangeFunction ->
            log.info("Utgående kall: {} {}", clientRequest.method(), clientRequest.url())
            log.info("Headers: {}", clientRequest.headers())
            log.info("Attributes: {}", clientRequest.attributes())

            next.exchange(clientRequest)
        }
    }
}