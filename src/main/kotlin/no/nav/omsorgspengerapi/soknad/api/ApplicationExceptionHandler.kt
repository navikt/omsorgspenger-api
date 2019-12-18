package no.nav.omsorgspengerapi.soknad.api

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.RuntimeException

@RestControllerAdvice(assignableTypes = [ApplicationController::class])
class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApplicationValidationException::class)
    fun handleApplicationValidationException(ex: ApplicationValidationException, request: ServerHttpRequest): OmsorgspengerAPIError {
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.BAD_REQUEST.value(),
                error = ex.javaClass.name,
                path = request.path.toString(),
                violations = ex.violations
        )
    }
}

class ApplicationValidationException(message: String, val violations: MutableSet<Violation>): RuntimeException(message)