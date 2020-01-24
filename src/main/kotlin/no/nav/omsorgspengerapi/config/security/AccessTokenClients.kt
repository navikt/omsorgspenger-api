package no.nav.omsorgspengerapi.config.security

import no.nav.helse.dusseldorf.oauth2.client.AccessTokenClient
import no.nav.helse.dusseldorf.oauth2.client.ClientSecretAccessTokenClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

@Configuration
internal class AccessTokenClients(
        @Value("\${nav.no.security.client.azure.client-id}") azureClientId: String,
        @Value("\${nav.no.security.client.azure.client-secret}") azureClientSecret: String,
        @Value("\${nav.no.security.client.azure.token-endpoint}") azureTokenEndpoint: URI
) {

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(AccessTokenClients::class.java)
    }

    init {
        logger.info("AzureClientId=$azureClientId")
        logger.info("AzureClientSecret=******")
        logger.info("AzureTokenEndpoint=$azureTokenEndpoint")
    }

    val azureV2AccessTokenClient = ClientSecretAccessTokenClient(
            clientId = azureClientId,
            clientSecret = azureClientSecret,
            tokenEndpoint = azureTokenEndpoint
    )

    @Bean
    @Qualifier("mottakTokenAccessClient")
    internal fun mottakTokenAccessClient(): AccessTokenClient = azureV2AccessTokenClient
}