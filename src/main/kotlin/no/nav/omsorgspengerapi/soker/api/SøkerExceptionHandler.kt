package no.nav.omsorgspengerapi.soker.api

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [SøkerController::class])
class SøkerExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SøkerOppslagException::class)
    fun handleSøkerOppslagException(ex: SøkerOppslagException, request: ServerHttpRequest): OmsorgspengerAPIError {
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }
}

class SøkerOppslagException(message: String) : RuntimeException(message)
