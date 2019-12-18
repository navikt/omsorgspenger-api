package no.nav.omsorgspengerapi

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientResponseException

@RestControllerAdvice
class OmsorgspengerApiExceptionHandler {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(OmsorgspengerApiExceptionHandler::class.java)
    }

    /**
     * Handles all upstream errors as INTERNAL_SERVER_ERROR.
     * However, the correct message amd response code from upstream are displayed in the response body.
     */
    @ExceptionHandler(WebClientResponseException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleWebClientResponseException(ex: WebClientResponseException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.error(ex.message, ex)
        return OmsorgspengerAPIError(
                message = ex.message,
                status = ex.rawStatusCode,
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }
}