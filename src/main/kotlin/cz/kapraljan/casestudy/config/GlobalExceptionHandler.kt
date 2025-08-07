package cz.kapraljan.casestudy.config

import cz.kapraljan.casestudy.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val details: String? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ClientErrorException::class)
    fun handleClientError(ex: ClientErrorException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Požadavek nelze zpracovat",
            message = ex.message ?: "Client error occurred",
            details = "Please verify your request parameters and try again"
        )

        val correlationId = request.getHeader("correlation-id")
        val responseBuilder = ResponseEntity.status(HttpStatus.BAD_REQUEST)

        if (correlationId != null) {
            responseBuilder.header("correlation-id", correlationId)
        }

        return responseBuilder.body(errorResponse)
    }

    @ExceptionHandler(ServerErrorException::class)
    fun handleServerError(ex: ServerErrorException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_GATEWAY.value(),
            error = "External Service Error",
            message = ex.message ?: "External service is experiencing issues",
            details = "The external service is temporarily unavailable. Please try again later."
        )

        val correlationId = request.getHeader("correlation-id")
        val responseBuilder = ResponseEntity.status(HttpStatus.BAD_GATEWAY)

        if (correlationId != null) {
            responseBuilder.header("correlation-id", correlationId)
        }

        return responseBuilder.body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericError(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred",
            details = "Please try again later or contact support if the problem persists"
        )

        val correlationId = request.getHeader("correlation-id")
        val responseBuilder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

        if (correlationId != null) {
            responseBuilder.header("correlation-id", correlationId)
        }

        return responseBuilder.body(errorResponse)
    }
}
