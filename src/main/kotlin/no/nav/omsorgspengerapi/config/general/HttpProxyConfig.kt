package no.nav.omsorgspengerapi.config.general

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class HttpProxyConfig {

    @Value("\${nav.no.http-proxy}")
    lateinit var httpProxy: String

    companion object {
        private val log: Logger = LoggerFactory.getLogger(HttpProxyConfig::class.java)
    }

    @PostConstruct
    fun log() {
        log.info("Got hhtpProxy: {}", httpProxy)
    }

}