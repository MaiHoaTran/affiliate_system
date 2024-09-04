package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.Commission.Companion.DEFAULT_COMMISSION_AMOUNT
import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.referral.ReferralRepository
import org.springframework.stereotype.Service
import java.time.Clock

/**
 * Use case to create commission by given referral user id:
 * - If cannot find any referrals by user id, do nothing
 * - If the commission with the user id is already created, do nothing
 * - Otherwise, create the new commission with default status as pending and has amount as [DEFAULT_COMMISSION_AMOUNT]
 */
@Service
class CreateCommissionForReferredUserId(
    private val clock: Clock,
    private val referralRepository: ReferralRepository,
    private val commissionRepository: CommissionRepository
) {
    /**
     * @param referredUserId the user id
     */
    operator fun invoke(referredUserId: Long) {
        val referralId = referralRepository.findByReferredUserId(referredUserId)?.id ?: return
        commissionRepository.findByReferralId(referralId)?.let { return }

        val now = clock.instant()
        val commission = Commission(
            referralId = referralId,
            amount = DEFAULT_COMMISSION_AMOUNT,
            status = CommissionStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )

        commissionRepository.upsert(commission)
    }
}
