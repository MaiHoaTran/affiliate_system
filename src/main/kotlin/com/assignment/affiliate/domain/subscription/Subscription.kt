package com.assignment.affiliate.domain.subscription

import java.time.Instant

data class Subscription(
    val id: Long? = null,
    val userId: Long,
    val status: SubscriptionStatus,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

enum class SubscriptionStatus(val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    SUSPENDED("suspended"),
    CANCELED("canceled"),
    EXPIRED("expired")
}

fun String.toSubscriptionStatus(): SubscriptionStatus = SubscriptionStatus.entries.firstOrNull { this == it.value }
    ?: throw IllegalArgumentException("Invalid subscription status")
