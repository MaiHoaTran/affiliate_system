package com.assignment.affiliate.domain.referral

import com.assignment.affiliate.usecases.referral.ReferralDTO

/**
 * The referral repository performs data operations related to referrals table
 */
interface ReferralRepository {
    /**
     * Find referral by referred user id
     *
     * @param referredUserId the referred user id
     * @return [Referral] or null
     */
    fun findByReferredUserId(referredUserId: Long): Referral?

    /**
     * Find all referrals by affiliate id
     *
     * @param affiliateId the affiliate id
     * @return list of [Referral]
     */
    fun findAllByAffiliateId(affiliateId: Long): Collection<Referral>

    /**
     * Get all referrals by user id
     *
     * @param userId the user id
     * @return list of [ReferralDTO]
     */
    fun getAllReferralDTOsByUserId(userId: Long): List<ReferralDTO>

    /**
     * Insert the given referral
     *
     * @param referral the referral to be inserted
     * @return the inserted [Referral]]
     */
    fun save(referral: Referral): Referral
}
