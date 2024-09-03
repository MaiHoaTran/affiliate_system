package com.assignment.affiliate.usecases.signup

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserDTO(
    val email: String,
    val affiliateCode: String
)
