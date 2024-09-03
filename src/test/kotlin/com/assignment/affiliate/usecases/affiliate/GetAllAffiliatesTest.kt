package com.assignment.affiliate.usecases.affiliate

import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetAllAffiliatesTest {

    private val affiliateRepository = mockk<AffiliateRepository>()
    private val getAllAffiliates = GetAllAffiliates(affiliateRepository)

    @Test
    fun `should return list of AffiliateDTO`() {
        val affiliate1 = mockk<AffiliateDTO>()
        val affiliate2 = mockk<AffiliateDTO>()
        val affiliates = listOf(affiliate1, affiliate2)

        every { affiliateRepository.findAll() } returns affiliates

        val result = getAllAffiliates()

        assertEquals(affiliates, result)
        verify { affiliateRepository.findAll() }
    }
}
