package com.assignment.affiliate.domain.commission

import com.fasterxml.jackson.annotation.JsonValue
import java.time.Instant

data class Commission(
    val id: Long? = null,
    val referralId: Long,
    val amount: Double,
    val status: CommissionStatus,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
) {
    companion object {
        const val DEFAULT_COMMISSION_AMOUNT = 19.0
    }
}

enum class CommissionStatus(val value: String) {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    PAID("paid"),
    FAILED("failed");

    @JsonValue
    fun toValue(): String {
        return value
    }
}

fun String.toCommissionStatus(): CommissionStatus = CommissionStatus.entries.firstOrNull { this == it.value }
    ?: throw IllegalArgumentException("Invalid commission status")
