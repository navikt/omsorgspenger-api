package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.common.OmsorgspengerAPIError
import no.nav.omsorgspengerapi.vedlegg.exception.AttachmentNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice(assignableTypes = [AttachmentController::class])
class AttachmentExceptionHandler {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentExceptionHandler::class.java)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AttachmentNotFoundException::class)
    fun handleAttachmentNotFound(ex: AttachmentNotFoundException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.warn(ex.message, ex)
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.NOT_FOUND.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(ex: Exception, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.error(ex.message, ex)
        return OmsorgspengerAPIError(
                message = "Something unexpected has happened.",
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }
}
