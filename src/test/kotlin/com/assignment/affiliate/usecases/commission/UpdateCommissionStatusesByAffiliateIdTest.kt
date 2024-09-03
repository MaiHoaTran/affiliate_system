package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Instant

class UpdateCommissionStatusesByAffiliateIdTest {
    private val commissionRepository = mockk<CommissionRepository>()
    private val updateCommissionStatusesByAffiliateId = UpdateCommissionStatusesByAffiliateId(commissionRepository)

    @Test
    fun `should update commission statuses when commissions exist for affiliate`() {
        // Arrange
        val affiliateId = 123L
        val referralId = 1L
        val newStatus = CommissionStatus.APPROVED
        val commissions = listOf(
            Commission(
                id = 1,
                amount = 100.0,
                status = CommissionStatus.PENDING,
                referralId = referralId,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ),
            Commission(
                id = 2,
                amount = 150.0,
                status = CommissionStatus.PENDING,
                referralId = referralId,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        val commissionIds = commissions.map { it.id!! }

        every { commissionRepository.findAllByAffiliateId(affiliateId) } returns commissions
        every { commissionRepository.updateStatus(commissionIds, newStatus) } just Runs

        updateCommissionStatusesByAffiliateId(affiliateId, newStatus)

        verify { commissionRepository.updateStatus(commissionIds, newStatus) }
    }

    @Test
    fun `should not update statuses when no commissions found for affiliate`() {
        val affiliateId = 123L
        val newStatus = CommissionStatus.APPROVED

        every { commissionRepository.findAllByAffiliateId(affiliateId) } returns emptyList()

        updateCommissionStatusesByAffiliateId(affiliateId, newStatus)

        verify(exactly = 0) { commissionRepository.updateStatus(any(), any()) }
    }
}
