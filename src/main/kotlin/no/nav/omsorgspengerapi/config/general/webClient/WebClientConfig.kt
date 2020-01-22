package no.nav.omsorgspengerapi.config.general.webClient

import no.nav.omsorgspengerapi.config.general.HttpProxyConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.*
import reactor.netty.tcp.ProxyProvider
import reactor.netty.tcp.TcpClient
import reactor.retry.Retry
import reactor.retry.RetryContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.Duration


@Configuration
class WebClientConfig(val proxyConfig: HttpProxyConfig) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(WebClientConfig::class.java)

        /**
         * Retry function configured with exponential backoff, with at most 3 retries and max 2 seconds of backoff.
         * Retries are performed after a backoff interval of firstBackoff * (2 ^ n) where n is the next iteration number.
         * Example:
         * * 1st. Retry: 400 ms
         * * 2nd. Retry: 800 ms
         * * 3rd. Retry: 1 600 ms
         */
        val retry = Retry.onlyIf<Any>(this::is5xxServerError)
                .retryMax(3)
                .exponentialBackoff(Duration.ofMillis(200), Duration.ofSeconds(2))
                .doOnRetry { log.info(it.toString()) }

        private fun is5xxServerError(retryContext: RetryContext<Any>): Boolean {
            return retryContext.exception() is WebClientResponseException &&
                    (retryContext.exception() as WebClientResponseException).statusCode.is5xxServerError
        }
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
        logger.info("Upstream request: {} {}", clientRequest.method(), URLDecoder.decode(clientRequest.url().toString(), StandardCharsets.UTF_8))
        logger.info("Headers: {}", clientRequest.headers().filter { it.key != "x-nav-apiKey" && it.key != HttpHeaders.AUTHORIZATION })

        val response = next.exchange(clientRequest)
        response.subscribe { logger.info("Upstream response: ${it.rawStatusCode()} from ${clientRequest.url()}")}

        response
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