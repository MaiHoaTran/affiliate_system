package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.CommissionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetAllCommissionsByAffiliateIdTest {

    private val commissionRepository = mockk<CommissionRepository>()
    private val getAllCommissionsByAffiliateId = GetAllCommissionsByAffiliateId(commissionRepository)

    @Test
    fun `should return list of CommissionDTOs`() {
        val affiliateId = 123L
        val commission1 = mockk<CommissionDTO>()
        val commission2 = mockk<CommissionDTO>()
        val expectedCommissions = listOf(commission1, commission2)

        every { commissionRepository.getAllCommissionDTOsByAffiliateId(affiliateId) } returns expectedCommissions

        val result = getAllCommissionsByAffiliateId(affiliateId)

        assertEquals(expectedCommissions, result)
        verify { commissionRepository.getAllCommissionDTOsByAffiliateId(affiliateId) }
    }
}
