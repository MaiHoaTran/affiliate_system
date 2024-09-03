package com.assignment.affiliate.domain.exception

class AffiliateCodeNotFoundException(message: String) : ResourceNotFoundException(
    ErrorCode.RESOURCE_NOT_FOUND,
    message
)
