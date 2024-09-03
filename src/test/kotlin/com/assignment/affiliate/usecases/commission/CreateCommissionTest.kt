package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.Commission.Companion.DEFAULT_COMMISSION_AMOUNT
import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.referral.Referral
import com.assignment.affiliate.domain.referral.ReferralRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant

class CreateCommissionTest {
    private val clock = mockk<Clock>()
    private val referralRepository = mockk<ReferralRepository>()
    private val commissionRepository = mockk<CommissionRepository>()
    private val createCommission = CreateCommission(clock, referralRepository, commissionRepository)

    @Test
    fun `should create commission when referral exists and no existing commission`() {
        val userId = 123L
        val referralId = 456L
        val now = Instant.now()
        val referral = Referral(id = referralId, affiliateId = 1, referredUserId = userId)
        val commission = Commission(
            referralId = referralId,
            amount = DEFAULT_COMMISSION_AMOUNT,
            status = CommissionStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )

        every { referralRepository.findByReferredUserId(userId) } returns referral
        every { commissionRepository.findByReferralId(referralId) } returns null
        every { clock.instant() } returns now
        every { commissionRepository.upsert(commission) } returns commission

        createCommission(userId)

        verify { commissionRepository.upsert(commission) }
    }

    @Test
    fun `should not create commission when no referral found`() {
        val userId = 123L
        every { referralRepository.findByReferredUserId(userId) } returns null

        createCommission(userId)

        verify(exactly = 0) { commissionRepository.upsert(any()) }
    }

    @Test
    fun `should not create commission when existing commission found`() {
        val userId = 123L
        val referralId = 456L
        val referral = Referral(id = referralId, affiliateId = 1, referredUserId = userId)
        val existingCommission = Commission(
            referralId = referralId,
            amount = DEFAULT_COMMISSION_AMOUNT,
            status = CommissionStatus.PENDING,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        every { referralRepository.findByReferredUserId(userId) } returns referral
        every { commissionRepository.findByReferralId(referralId) } returns existingCommission

        createCommission(userId)

        verify(exactly = 0) { commissionRepository.upsert(any()) }
    }
}
