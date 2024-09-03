package com.assignment.affiliate.usecases.referral

import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import com.assignment.affiliate.domain.exception.AffiliateCodeNotFoundException
import com.assignment.affiliate.domain.referral.Referral
import com.assignment.affiliate.domain.referral.ReferralRepository
import org.springframework.stereotype.Service

/**
 * Use case to create referral by given user id and refCode
 */
@Service
class CreateReferral(
    private val affiliateRepository: AffiliateRepository,
    private val referralRepository: ReferralRepository
) {
    /**
     * @param userId the user id
     * @param refCode the referral code
     */
    operator fun invoke(userId: Long, refCode: String?) {
        if (refCode == null) {
            return
        }

        val refAffiliate = affiliateRepository.findByAffiliateCode(refCode)
            ?: throw AffiliateCodeNotFoundException("Cannot found affiliate with refCode: $refCode")

        val referral = Referral(
            affiliateId = refAffiliate.id!!,
            referredUserId = userId
        )

        referralRepository.save(referral)
    }
}
