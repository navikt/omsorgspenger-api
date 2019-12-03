package no.nav.omsorgspengerapi.config.general

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
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
class K9LookupClient(private val proxyConfig: HttpProxyConfig) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9LookupClient::class.java)
    }

    @Bean()
    protected fun webClient(): WebClient {
        val reactorClientHttpConnector = ReactorClientHttpConnector(HttpClient.create()
                .tcpConfiguration { tcpClient: TcpClient ->
                    resolveProxieSettings(tcpClient)
                })

        return WebClient.builder()
                .clientConnector(reactorClientHttpConnector)
                .defaultHeader("Accept", "application/json")
                .filter(logRequest())
                .filter(ServerBearerExchangeFilterFunction())
                .build()
    }

    private fun resolveProxieSettings(tcpClient: TcpClient): TcpClient? {
        return if (proxyConfig.httpProxyHost == "localhost" && proxyConfig.httpProxyPort.toInt() == 8080) {
            tcpClient
        } else {
            tcpClient.proxy { proxy: ProxyProvider.TypeSpec ->
                proxy.type(ProxyProvider.Proxy.HTTP)
                        .host(proxyConfig.httpProxyHost)
                        .port(proxyConfig.httpProxyPort.toInt())
                        .nonProxyHosts(proxyConfig.httpNonProxyHosts)
            }
        }
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