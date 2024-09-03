package com.assignment.affiliate.domain.referral

import java.time.Instant

data class Referral(
    val id: Long? = null,
    val affiliateId: Long,
    val referredUserId: Long,
    val createdAt: Instant? = null
)
