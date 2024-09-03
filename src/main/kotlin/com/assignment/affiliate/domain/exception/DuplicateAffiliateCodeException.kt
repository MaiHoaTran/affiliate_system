package com.assignment.affiliate.domain.exception

class DuplicateAffiliateCodeException : InputValidationException(
    "An affiliate with the same affiliate code already exists",
    listOf(Error(field = "affiliate_code", errorCode = ErrorCode.DUPLICATE_AFFILIATE_CODE))
)
