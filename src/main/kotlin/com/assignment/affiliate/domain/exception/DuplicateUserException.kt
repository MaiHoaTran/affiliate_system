package com.assignment.affiliate.domain.exception

class DuplicateUserException : InputValidationException(
    "An user with the same email already exists",
    listOf(Error(field = "email", errorCode = ErrorCode.DUPLICATE_USER))
)
