package com.assignment.affiliate.domain.exception

abstract class ResourceNotFoundException(val errorCode: ErrorCode, message: String) : RuntimeException(message)
