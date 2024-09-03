package com.assignment.affiliate.usecases.affiliate

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AffiliateDTO(
    val affiliateId: Long,
    val affiliateCode: String,
    val affiliateUserId: Long,
    val affiliateUserEmail: String
)
