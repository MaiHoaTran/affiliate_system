package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.exception.CommissionNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.Instant

class UpdateCommissionStatusByCommissionIdTest {
    private val clock = mockk<Clock>()
    private val commissionRepository = mockk<CommissionRepository>()
    private val updateCommissionStatus = UpdateCommissionStatusByCommissionId(clock, commissionRepository)

    @Test
    fun `should update commission status when commission exists`() {
        val commissionId = 123L
        val referralId = 1L
        val newStatus = CommissionStatus.APPROVED
        val now = Instant.now()
        val existingCommission = Commission(
            id = commissionId,
            amount = Commission.DEFAULT_COMMISSION_AMOUNT,
            referralId = referralId,
            status = CommissionStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )
        val updatedCommission = existingCommission.copy(
            status = newStatus,
            updatedAt = now
        )

        every { commissionRepository.findById(commissionId) } returns existingCommission
        every { clock.instant() } returns now
        every { commissionRepository.upsert(updatedCommission) } returns updatedCommission

        updateCommissionStatus(commissionId, newStatus)

        verify { commissionRepository.upsert(updatedCommission) }
    }

    @Test
    fun `should throw exception when commission not found`() {
        // Arrange
        val commissionId = 123L
        val newStatus = CommissionStatus.APPROVED

        every { commissionRepository.findById(commissionId) } returns null

        assertThrows<CommissionNotFoundException> {
            updateCommissionStatus(commissionId, newStatus)
        }
    }
}
