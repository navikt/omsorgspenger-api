package no.nav.omsorgspengerapi.config.general

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class HttpProxyConfig {

    @Value("\${http.proxyHost}")
    lateinit var httpProxyHost: String

    @Value("\${http.proxyPort}")
    lateinit var httpProxyPort: Integer

    @Value("\${http.nonProxyHosts}")
    lateinit var httpNonProxyHosts: String
    @Value("\${https.proxyHost}")
    lateinit var httpsProxyHost: String

    @Value("\${https.proxyPort}")
    lateinit var httpsProxyPort: String

    companion object {
        private val log: Logger = LoggerFactory.getLogger(HttpProxyConfig::class.java)
    }

    @PostConstruct
    fun log() {
        httpNonProxyHosts = httpNonProxyHosts.replace("*", "\\*")
        log.info("http.proxyHost={}", httpProxyHost)
        log.info("http.proxyPort={}", httpProxyPort)
        log.info("http.nonProxyHosts={}", httpNonProxyHosts)
        log.info("https.proxyHost={}", httpsProxyHost)
        log.info("https.proxyHost={}", httpsProxyPort)


    }
}
