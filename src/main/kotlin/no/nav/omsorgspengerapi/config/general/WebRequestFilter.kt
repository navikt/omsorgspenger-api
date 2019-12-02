package no.nav.omsorgspengerapi.config.general

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*

@Component
class WebRequestFilter : WebFilter {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(WebRequestFilter::class.java)
    }

    /**
     * Process the Web request and (optionally) delegate to the next
     * `WebFilter` through the given [WebFilterChain].
     * @param exchange the current server exchange
     * @param chain provides a way to delegate to the next filter
     * @return `Mono<Void>` to indicate when request processing is complete
     */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.path.toString().toLowerCase()

        // Ignore actuator endpoint requests...
        if (path.contains("actuator")) {
            return chain.filter(exchange)
        }

        val requestId = UUID.randomUUID()
        log.info("Genererer requestId: {}", requestId)

        exchange.response.headers.add("X-Request-Id", "generated-${requestId}")

        return chain.filter(exchange)
    }
}