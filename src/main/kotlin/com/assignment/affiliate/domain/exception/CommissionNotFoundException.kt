package com.assignment.affiliate.domain.exception

class CommissionNotFoundException(message: String) : ResourceNotFoundException(
    ErrorCode.RESOURCE_NOT_FOUND,
    message
)
