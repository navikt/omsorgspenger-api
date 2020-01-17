package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice(assignableTypes = [VedleggController::class])
class DokumentExceptionHandler {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(DokumentExceptionHandler::class.java)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(VedleggIkkeFunnetException::class)
    fun handleDokumentIkkeFunnetException(ex: VedleggIkkeFunnetException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.warn(ex.message, ex)
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.NOT_FOUND.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(VedleggHentingFeiletException::class)
    fun handleDokumentHentingFeiletException(ex: VedleggHentingFeiletException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.warn(ex.message, ex)
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.NOT_FOUND.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(VedleggtypeIkkeSupportertException::class)
    fun handleDokumenttypeIkkeSupportertException(ex: VedleggtypeIkkeSupportertException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.warn(ex.message, ex)
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.NOT_FOUND.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DecodingException::class)
    fun handleDecodingException(ex: DecodingException, request: ServerHttpRequest): OmsorgspengerAPIError {
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.BAD_REQUEST.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }
}

class VedleggtypeIkkeSupportertException(message: String) : RuntimeException(message)
class VedleggIkkeFunnetException(message: String) : RuntimeException(message)
class VedleggOpplastingFeiletException(message: String) : RuntimeException(message)
class VedleggHentingFeiletException(message: String) : RuntimeException(message)
class VedleggSlettingFeiletException(message: String) : RuntimeException(message)