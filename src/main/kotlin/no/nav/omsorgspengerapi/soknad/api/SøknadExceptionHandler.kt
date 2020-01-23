package no.nav.omsorgspengerapi.soknad.api

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [SøknadController::class])
class SøknadExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SøknadValideringException::class)
    fun handleASøknadValideringExceptionn(ex: SøknadValideringException, request: ServerHttpRequest): OmsorgspengerAPIError {
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.BAD_REQUEST.value(),
                error = ex.javaClass.name,
                path = request.path.toString(),
                violations = ex.violations
        )
    }
}

class SøknadInnsendingFeiletException(message: String) : RuntimeException(message)
class SøknadValideringException(message: String, val violations: MutableSet<Violation>) : RuntimeException(message)
