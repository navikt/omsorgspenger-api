package no.nav.omsorgspenger

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.routing.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.helse.dusseldorf.ktor.auth.*
import no.nav.helse.dusseldorf.ktor.client.HttpRequestHealthCheck
import no.nav.helse.dusseldorf.ktor.client.HttpRequestHealthConfig
import no.nav.helse.dusseldorf.ktor.client.buildURL
import no.nav.helse.dusseldorf.ktor.core.*
import no.nav.helse.dusseldorf.ktor.health.HealthReporter
import no.nav.helse.dusseldorf.ktor.health.HealthRoute
import no.nav.helse.dusseldorf.ktor.health.HealthService
import no.nav.helse.dusseldorf.ktor.jackson.JacksonStatusPages
import no.nav.helse.dusseldorf.ktor.jackson.dusseldorfConfigured
import no.nav.helse.dusseldorf.ktor.metrics.MetricsRoute
import no.nav.helse.dusseldorf.ktor.metrics.init
import no.nav.helse.dusseldorf.oauth2.client.CachedAccessTokenClient
import no.nav.k9.kafka.KafkaProducer
import no.nav.omsorgspenger.barn.BarnGateway
import no.nav.omsorgspenger.barn.BarnService
import no.nav.omsorgspenger.barn.barnApis
import no.nav.omsorgspenger.general.systemauth.AccessTokenClientResolver
import no.nav.omsorgspenger.mellomlagring.MellomlagringService
import no.nav.omsorgspenger.mellomlagring.mellomlagringApis
import no.nav.omsorgspenger.redis.RedisConfig
import no.nav.omsorgspenger.redis.RedisStore
import no.nav.omsorgspenger.soker.SøkerGateway
import no.nav.omsorgspenger.soker.SøkerService
import no.nav.omsorgspenger.soker.søkerApis
import no.nav.omsorgspenger.soknad.SøknadService
import no.nav.omsorgspenger.soknad.søknadApis
import no.nav.omsorgspenger.vedlegg.K9MellomlagringGateway
import no.nav.omsorgspenger.vedlegg.VedleggService
import no.nav.omsorgspenger.vedlegg.vedleggApis
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val logger: Logger = LoggerFactory.getLogger("Omsorgspenger-api")

fun Application.omsorgpengesoknadapi() {

    val appId = environment.config.id()
    logProxyProperties()
    DefaultExports.initialize()

    System.setProperty("dusseldorf.ktor.serializeProblemDetailsWithContentNegotiation", "true")

    val configuration = Configuration(environment.config)
    val accessTokenClientResolver = AccessTokenClientResolver(environment.config.clients())
    val tokenxClient = CachedAccessTokenClient(accessTokenClientResolver.tokenxClient)

    install(ContentNegotiation) {
        jackson {
            dusseldorfConfigured()
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        log.info("Configuring CORS")
        configuration.getWhitelistedCorsAddreses().forEach {
            log.info("Adding host {} with scheme {}", it.host, it.scheme)
            host(host = it.authority, schemes = listOf(it.scheme))
        }
    }

    val idTokenProvider = IdTokenProvider(cookieName = configuration.getCookieName())
    val issuers = configuration.issuers()

    install(Authentication) {
       multipleJwtIssuers(
           issuers = issuers,
           extractHttpAuthHeader = { call -> idTokenProvider.getIdToken(call).somHttpAuthHeader() }
       )
    }

    install(StatusPages) {
        DefaultStatusPages()
        JacksonStatusPages()
        IdTokenStatusPages()
    }

    install(Routing) {
        val k9MellomlagringGateway = K9MellomlagringGateway(
            baseUrl = configuration.getK9MellomlagringUrl(),
            accessTokenClient = accessTokenClientResolver.azureV2AccessTokenClient,
            k9MellomlagringScope = configuration.getK9MellomlagringScopes(),
            exchangeTokenClient = tokenxClient,
            k9MellomlagringTokenxAudience = configuration.getK9MellomlagringTokenxAudience()

        )

        val vedleggService = VedleggService(k9MellomlagringGateway = k9MellomlagringGateway)

        val sokerGateway = SøkerGateway(
            baseUrl = configuration.getK9OppslagUrl(),
            exchangeTokenClient = tokenxClient,
            k9SelvbetjeningOppslagTokenxAudience = configuration.getK9SelvbetjeningOppslagTokenxAudience()
        )

        val barnGateway = BarnGateway(
            exchangeTokenClient = tokenxClient,
            k9SelvbetjeningOppslagTokenxAudience = configuration.getK9SelvbetjeningOppslagTokenxAudience(),
            baseUrl = configuration.getK9OppslagUrl()
        )

        val barnService = BarnService(
            barnGateway = barnGateway,
            cache = configuration.cache()
        )

        val søkerService = SøkerService(
            søkerGateway = sokerGateway
        )

        val kafkaProducer = KafkaProducer(
            kafkaConfig = configuration.getKafkaConfig()
        )

        environment.monitor.subscribe(ApplicationStopping) {
            logger.info("Stopper Kafka Producer.")
            kafkaProducer.stop()
            logger.info("Kafka Producer Stoppet.")
        }

        authenticate(*issuers.allIssuers()) {
            søkerApis(
                søkerService = søkerService,
                idTokenProvider = idTokenProvider
            )

            barnApis(
                barnService = barnService,
                idTokenProvider = idTokenProvider
            )

            mellomlagringApis(
                mellomlagringService = MellomlagringService(
                    RedisStore(
                        redisClient = RedisConfig.redisClient(
                            redisHost = configuration.getRedisHost(),
                            redisPort = configuration.getRedisPort()
                        )
                    ),
                    passphrase = configuration.getStoragePassphrase(),
                ),
                idTokenProvider = idTokenProvider
            )

            vedleggApis(
                vedleggService = vedleggService,
                idTokenProvider = idTokenProvider
            )

            søknadApis(
                idTokenProvider = idTokenProvider,
                søknadService = SøknadService(
                    vedleggService = vedleggService,
                    søkerService = søkerService,
                    barnService = barnService,
                    kafkaProducer = kafkaProducer
                ),
                søkerService = søkerService,
                barnService = barnService
            )
        }

        val healthService = HealthService(
            healthChecks = setOf(
                kafkaProducer,
                HttpRequestHealthCheck(
                    mapOf(
                        Url.buildURL(
                            baseUrl = configuration.getK9MellomlagringUrl(),
                            pathParts = listOf("health")
                        ) to HttpRequestHealthConfig(expectedStatus = HttpStatusCode.OK)
                    )
                )
            )
        )

        HealthReporter(
            app = appId,
            healthService = healthService,
            frequency = Duration.ofMinutes(1)
        )

        DefaultProbeRoutes()
        MetricsRoute()
        HealthRoute(
            healthService = healthService
        )
    }

    install(MicrometerMetrics) {
        init(appId)
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        call.request.log()
    }

    install(CallId) {
        generated()
    }

    install(CallLogging) {
        correlationIdAndRequestIdInMdc()
        logRequests()
        mdc("id_token_jti") { call ->
            try { idTokenProvider.getIdToken(call).getId() }
            catch (cause: Throwable) { null }
        }
    }
}

fun ObjectMapper.k9MellomlagringGatewayKonfigurert(): ObjectMapper {
    return jacksonObjectMapper().dusseldorfConfigured().apply {
        configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    }
}

fun ObjectMapper.k9SelvbetjeningOppslagKonfigurert(): ObjectMapper {
    return jacksonObjectMapper().dusseldorfConfigured().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        registerModule(JavaTimeModule())
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    }
}
