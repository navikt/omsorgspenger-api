package no.nav.omsorgspengerapi.config.general

import io.netty.handler.ssl.SslContextBuilder
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
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient
import reactor.netty.http.client.HttpClientResponse
import reactor.netty.tcp.ProxyProvider
import reactor.netty.tcp.SslProvider
import reactor.netty.tcp.TcpClient


@Configuration
class K9LookupClient {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(K9LookupClient::class.java)
    }

    /*@Bean
    fun httpClient(proxyConfig: HttpProxyConfig): HttpClient {
        val sslContext = SslContextBuilder
                .forClient()
                .sslProvider(io.netty.handler.ssl.SslProvider.JDK)
                .build()

        val httpClient = HttpClient.create()
                //.secure { ssl: SslProvider.SslContextSpec -> ssl.sslContext(sslContext) }
                .tcpConfiguration { tcpClient: TcpClient ->
                    tcpClient.proxy { proxy: ProxyProvider.TypeSpec ->
                        proxy.type(ProxyProvider.Proxy.HTTP)
                                .host(proxyConfig.httpProxyHost)
                                .port(proxyConfig.httpProxyPort.toInt())
                                .nonProxyHosts(proxyConfig.httpNonProxyHosts)
                    }
                }

        val res = httpClient.get()
                .uri("https://login.microsoftonline.com/navtestb2c.onmicrosoft.com/discovery/v2.0/keys?p=b2c_1a_idporten_ver1")
                .response()
                .block()

        log.info("res: {}", res?.status())

        return httpClient
    }*/

    @Bean()
    protected fun webClient(): WebClient {

        return WebClient.builder()
                //.clientConnector(reactorClientHttpConnector)
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