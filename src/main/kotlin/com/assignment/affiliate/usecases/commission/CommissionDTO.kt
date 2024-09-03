package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.CommissionStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CommissionDTO(
    val commissionId: Long,
    val commissionStatus: CommissionStatus,
    val commissionAmount: Double,
    @JsonIgnore
    val affiliateUserId: Long,
    val referredUserId: Long,
    val referredUserEmail: String
)
