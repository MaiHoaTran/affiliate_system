package com.assignment.affiliate.domain.affiliate

import java.time.Instant

data class Affiliate(
    val id: Long ? = null,
    val userId: Long,
    val affiliateCode: String,
    val createdAt: Instant? = null
) {
    companion object {
        const val AFFILIATE_CODE_LENGTH = 10
    }
}
