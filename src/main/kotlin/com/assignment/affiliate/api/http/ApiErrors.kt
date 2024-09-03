package com.assignment.affiliate.api.http

import com.assignment.affiliate.domain.exception.ErrorCode

/**
 * Encapsulates a list of [ApiError].
 *
 * This is serialized to JSON and included in response payload to REST API calls which result in an
 * error.
 */

data class ApiErrors(val errors: List<ApiError>)

/**
 * A single error from a REST API.
 *
 * This is serialized to JSON and included in response payload to REST API calls which result in an
 * error.
 */

data class ApiError(val id: String, val code: ErrorCode, val title: String, val detail: String? = null)
