package com.assignment.affiliate.domain.commission

data class PayoutCommission(
    val userEmail: String,
    val commissionId: Long,
    val commissionAmount: Double
)
