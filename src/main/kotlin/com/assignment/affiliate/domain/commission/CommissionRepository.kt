package com.assignment.affiliate.domain.commission

import com.assignment.affiliate.usecases.commission.CommissionDTO
import java.time.Instant

/**
 * Commission repository performs data operations related to commissions table
 */
interface CommissionRepository {
    /**
     * Find commission by id
     *
     * @param id the given commission id
     * @return [Commission] or null
     */
    fun findById(id: Long): Commission?

    /**
     * Find commission by given referral id
     *
     * @param referralId the referral id
     * @return [Commission] or null
     */
    fun findByReferralId(referralId: Long): Commission?

    /**
     * Find all commissions by status
     *
     * @param status the commission status [CommissionStatus]
     * @return list of [Commission]
     */
    fun findAllByStatus(status: CommissionStatus): List<Commission>

    /**
     * Find all commissions by affiliate id
     *
     * @param affiliateId the affiliate id
     * @return list of [Commission]
     */
    fun findAllByAffiliateId(affiliateId: Long): List<Commission>

    /**
     * Find all commissions by user id
     *
     * @param userId the user id
     * @return list of [Commission]
     */
    fun findAllByUserId(userId: Long): List<Commission>

    /**
     * Find all approved commissions which are ready to payout
     *
     * @return list of [Commission]
     */
    fun findAllToPayout(): List<PayoutCommission>

    /**
     * Get all commissions by affiliate id
     *
     * @return list of [CommissionDTO]
     */
    fun getAllCommissionDTOsByAffiliateId(affiliateId: Long): List<CommissionDTO>

    /**
     * Get all commissions by given status and created before or at given instant
     *
     * @param status the commission status [CommissionStatus]
     * @param instant the instant where the commission is created before or at [instant]
     * @return list of [CommissionDTO]
     */
    fun getAllCommissionDTOsByStatusAndCreatedBeforeOrAt(status: CommissionStatus, instant: Instant): List<CommissionDTO>

    /**
     * Insert or update the given commission
     *
     * @param commission the commission to be inserted/updated
     */
    fun upsert(commission: Commission): Commission

    /**
     * Update commission status for the given ids with the updated status
     *
     * @param ids the list of commission id which are need to update status
     * @param status the updated commission status
     */
    fun updateStatus(ids: Collection<Long>, status: CommissionStatus)

    /**
     * Update commission status for the given user id with the updated status
     *
     * @param userId the user id
     * @param status the updated commission status
     */
    fun sumByUserIdAndStatus(userId: Long, status: CommissionStatus): Double
}
