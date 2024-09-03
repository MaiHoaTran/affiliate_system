package com.assignment.affiliate.domain.exception

class UserNotFoundException(message: String) : ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, message)
