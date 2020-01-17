package no.nav.omsorgspengerapi.config.general.webClient

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import java.net.URI

@Configuration
class OmsorgspengerSoknadMottakWebClientConfig {

    @Value("\${nav.no.gateways.omsorgspengersoknad_mottak_url}")
    lateinit var baseUrl: URI

    companion object {
        private val log: Logger = LoggerFactory.getLogger(OmsorgspengerSoknadMottakWebClientConfig::class.java)
    }

    @Bean("søknadMottaksKlient")
    protected fun applicationReceiverClient(): WebClient {
        return WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(HttpClient.from(
                        TcpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                                .doOnConnected { connection ->
                                    connection
                                            .addHandlerLast(ReadTimeoutHandler(20))
                                            .addHandlerLast(WriteTimeoutHandler(20))
                                }
                )))
                .baseUrl(baseUrl.toString())
                .filter(logOutgoingRequest(log))
                .filter(ServerBearerExchangeFilterFunction())
                .build()
    }
}