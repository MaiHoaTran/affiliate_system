package com.assignment.affiliate.domain.exception

class DuplicateAffiliateUserException : InputValidationException(
    "An affiliate with the same user already exists",
    listOf(Error(field = "user_id", errorCode = ErrorCode.DUPLICATE_AFFILIATE_USER))
)
