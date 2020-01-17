package no.nav.omsorgspengerapi.barn.api

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [BarnController::class])
class ChildExceptionHandler {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ChildExceptionHandler::class.java)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BarnOppslagException::class)
    fun handleChildLookupFailedException(ex: BarnOppslagException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.error(ex.message, ex)

        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }
}

class BarnOppslagException(message: String) : RuntimeException(message)
