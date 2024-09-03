package com.assignment.affiliate.usecases.affiliate

import com.assignment.affiliate.domain.affiliate.Affiliate.Companion.AFFILIATE_CODE_LENGTH
import org.springframework.stereotype.Service

/**
 * Use case to generate affiliate code
 * - is case-sensitive and
 * - its length is [AFFILIATE_CODE_LENGTH]
 * - contains only number and character
 */
@Service
class GenerateAffiliateCode {
    operator fun invoke(): String {
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..AFFILIATE_CODE_LENGTH)
            .map { chars.random() }
            .joinToString("")
    }
}
