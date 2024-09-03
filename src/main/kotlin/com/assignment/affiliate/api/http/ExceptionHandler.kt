package com.assignment.affiliate.api.http

import com.assignment.affiliate.domain.exception.ErrorCode
import com.assignment.affiliate.domain.exception.InputValidationException
import com.assignment.affiliate.domain.exception.ResourceNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.UUID

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFoundException(exception: ResourceNotFoundException): ResponseEntity<ApiErrors> {
        val uuid = UUID.randomUUID().toString()
        val apiError = ApiError(
            uuid,
            ErrorCode.RESOURCE_NOT_FOUND,
            exception.message ?: HttpStatus.NOT_FOUND.reasonPhrase
        )

        logger.error(exception.message, exception)

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrors(listOf(apiError)))
    }

    @ExceptionHandler(IllegalStateException::class, IllegalArgumentException::class, InputValidationException::class)
    fun handleBadRequestException(exception: Exception): ResponseEntity<ApiErrors> {
        val uuid = UUID.randomUUID().toString()
        val errorMessage =
            if (exception is InputValidationException) {
                exception.errors.joinToString { "${it.field}: ${it.errorCode}" }
            } else {
                exception.message
            }

        val apiError = ApiError(
            uuid,
            ErrorCode.BAD_REQUEST,
            errorMessage ?: HttpStatus.BAD_REQUEST.reasonPhrase
        )

        logger.error(exception.message, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiErrors(listOf(apiError)))
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrors> {
        val uuid = UUID.randomUUID().toString()
        val apiError = ApiError(
            uuid,
            ErrorCode.SERVER_ERROR,
            "Something went wrong: ${ex.message}"
        )

        logger.error("Error processing request - uncaught exception, ID: $uuid", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrors(listOf(apiError)))
    }
}
