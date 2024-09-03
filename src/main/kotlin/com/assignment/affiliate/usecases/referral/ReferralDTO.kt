package com.assignment.affiliate.usecases.referral

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ReferralDTO(
    val referralId: Long,
    val referralStatus: ReferralStatus,
    val referredUserId: Long,
    val referredUserEmail: String,
    val commissionAmount: Double?
)

enum class ReferralStatus(val value: String) {
    CONVERTED("converted"),
    PENDING("pending");

    @JsonValue
    fun toValue(): String {
        return value
    }
}

fun String.toReferralStatus(): ReferralStatus = ReferralStatus.entries.firstOrNull { this == it.value }
    ?: throw IllegalArgumentException("Invalid referral status")
