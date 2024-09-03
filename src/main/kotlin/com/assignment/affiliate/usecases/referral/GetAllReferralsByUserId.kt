package com.assignment.affiliate.usecases.referral

import com.assignment.affiliate.domain.referral.ReferralRepository
import org.springframework.stereotype.Service

/**
 * Use case to get all referrals by affiliate user id
 */
@Service
class GetAllReferralsByUserId(
    private var referralRepository: ReferralRepository
) {
    /**
     * @param affiliateUserId the affiliate user id
     * @return the list of [ReferralDTO]
     */
    operator fun invoke(affiliateUserId: Long): List<ReferralDTO> {
        return referralRepository.getAllReferralDTOsByUserId(affiliateUserId)
    }
}
