package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CalculateCommissionAmountByUserIdAndStatusTest {

    private val commissionRepository = mockk<CommissionRepository>()
    private val calculateCommissionAmount = CalculateCommissionAmountByUserIdAndStatus(commissionRepository)

    @Test
    fun `should return correct commission amount`() {
        val userId = 123L
        val status = CommissionStatus.PAID
        val expectedAmount = 150.0

        every { commissionRepository.sumByUserIdAndStatus(userId, status) } returns expectedAmount

        val result = calculateCommissionAmount(userId, status)

        assertEquals(expectedAmount, result)
        verify { commissionRepository.sumByUserIdAndStatus(userId, status) }
    }
}
