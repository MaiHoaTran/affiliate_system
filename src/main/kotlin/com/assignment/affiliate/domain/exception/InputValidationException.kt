package com.assignment.affiliate.domain.exception

open class InputValidationException(message: String, val errors: List<Error>) : RuntimeException(message)

data class Error(
    val field: String,
    val errorCode: ErrorCode
)
