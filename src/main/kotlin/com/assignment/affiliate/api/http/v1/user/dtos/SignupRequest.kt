package com.assignment.affiliate.api.http.v1.user.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SignupRequest(
    val email: String,
    val password: String,
    val refCode: String? = null
)
