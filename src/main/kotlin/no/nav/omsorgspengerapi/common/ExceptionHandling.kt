package no.nav.omsorgspengerapi.common

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.LocalDateTime

data class OmsorgspengerAPIError(
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val error: String? = "no error available",
        val message: String? = "no message available",
        val path: String? = "no path available",
        val status: Int? = 500) {

    companion object {
        fun fromDefaultAttributeMap(defaultErrorAttributes: Map<String, Any>): OmsorgspengerAPIError {
            return OmsorgspengerAPIError(
                    timestamp = LocalDateTime.now(),
                    error = defaultErrorAttributes["error"] as String,
                    message = defaultErrorAttributes["message"] as String,
                    path = defaultErrorAttributes["path"] as String,
                    status = defaultErrorAttributes["status"] as Int
            )
        }
    }

    fun toAttributeMap(): Map<String, Any> {
        return java.util.Map.of<String, Any>(
                "timestamp", timestamp,
                "error", error,
                "message", message,
                "path", path,
                "status", status
        )
    }
}

internal class OmsorgspengerAPIErrorErrorAttributes() : DefaultErrorAttributes() {
    override fun getErrorAttributes(serverRequest: ServerRequest, includeStackTrace: Boolean): Map<String, Any> {
        val defaultErrorAttributes: Map<String, Any> = super.getErrorAttributes(serverRequest, false)
        val error: OmsorgspengerAPIError = OmsorgspengerAPIError
                .fromDefaultAttributeMap(defaultErrorAttributes)
        return error.toAttributeMap()
    }
}