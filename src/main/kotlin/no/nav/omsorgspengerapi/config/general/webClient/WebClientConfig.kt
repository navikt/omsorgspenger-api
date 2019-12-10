package no.nav.omsorgspengerapi.config.general.webClient

import no.nav.omsorgspengerapi.config.general.HttpProxyConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.tcp.ProxyProvider
import reactor.netty.tcp.TcpClient
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Configuration
class WebClientConfig(val proxyConfig: HttpProxyConfig) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(WebClientConfig::class.java)
    }

    /**
     * Default webClient.
     * Will be used if a Qualifier is not provided.
     */
    @Bean("defaultWebClient")
    @Primary
    protected fun defaultWebClient(): WebClient {
        return WebClient.builder()
                .defaultHeader("Accept", "application/json")
                .filter(logOutgoingRequest(log))
                .filter(ServerBearerExchangeFilterFunction())
                .build()
    }

}

fun logOutgoingRequest(logger: Logger): ExchangeFilterFunction {
    return ExchangeFilterFunction { clientRequest: ClientRequest, next: ExchangeFunction ->
        logger.info("Utgående kall: {} {}", clientRequest.method(), URLDecoder.decode(clientRequest.url().toString(), StandardCharsets.UTF_8))
        logger.info("Headers: {}", clientRequest.headers().filter { it.key != "x-nav-apiKey" })

        next.exchange(clientRequest)
    }
}

/**
 * Resolves and configures a TCP CLient with proxy settings
 */
 fun WebClientConfig.resolveProxySettings(tcpClient: TcpClient): TcpClient {
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