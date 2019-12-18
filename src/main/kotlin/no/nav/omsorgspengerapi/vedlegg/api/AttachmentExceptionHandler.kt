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


@RestControllerAdvice(assignableTypes = [AttachmentController::class])
class AttachmentExceptionHandler {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentExceptionHandler::class.java)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DocumentNotFoundException::class)
    fun handleAttachmentNotFound(ex: DocumentNotFoundException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.warn(ex.message, ex)
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.NOT_FOUND.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DocumentRetrievalFailedException::class)
    fun handleDocumentRetrievalFailedException(ex: DocumentRetrievalFailedException, request: ServerHttpRequest): OmsorgspengerAPIError {
        log.warn(ex.message, ex)
        return OmsorgspengerAPIError(
                message = ex.message,
                status = HttpStatus.NOT_FOUND.value(),
                error = ex.javaClass.name,
                path = request.path.toString()
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DocumentContentTypeNotSupported::class)
    fun handleDocumentContentTypeNotSupported(ex: DocumentContentTypeNotSupported, request: ServerHttpRequest): OmsorgspengerAPIError {
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

class DocumentContentTypeNotSupported(message: String) : RuntimeException(message)
class DocumentNotFoundException(message: String) : RuntimeException(message)
class DocumentUploadFailedException(message: String) : RuntimeException(message)
class DocumentRetrievalFailedException(message: String) : RuntimeException(message)
class DocumentDeletionFailedException(message: String) : RuntimeException(message)