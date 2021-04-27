package no.nav.omsorgspenger

import com.github.fppt.jedismock.RedisServer
import io.ktor.server.testing.*
import no.nav.helse.dusseldorf.testsupport.asArguments
import no.nav.helse.dusseldorf.testsupport.wiremock.WireMockBuilder
import no.nav.omsorgspenger.mellomlagring.started
import no.nav.omsorgspenger.wiremock.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ApplicationWithMocks {
    companion object {

        private val logger: Logger = LoggerFactory.getLogger(ApplicationWithMocks::class.java)

        @JvmStatic
        fun main(args: Array<String>) {

            val wireMockServer = WireMockBuilder()
                .withPort(8081)
                .withAzureSupport()
                .withNaisStsSupport()
                .withLoginServiceSupport()
                .omsorgspengesoknadApiConfig()
                .build()
                .stubOmsorgsoknadMottakHealth()
                .stubOppslagHealth()
                .stubLeggSoknadTilProsessering("v1/soknad")
                .stubK9Mellomlagring()
                .stubK9OppslagSoker()
                .stubK9OppslagBarn()

            val redisServer: RedisServer = RedisServer
                .newRedisServer()
                .started()

            val testArgs = TestConfiguration.asMap(
                port = 8082,
                wireMockServer = wireMockServer,
                redisServer = redisServer
            ).asArguments()

            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    logger.info("Tearing down")
                    wireMockServer.stop()
                    logger.info("Tear down complete")
                }
            })

            withApplication { no.nav.omsorgspenger.main(testArgs) }
        }
    }
}
