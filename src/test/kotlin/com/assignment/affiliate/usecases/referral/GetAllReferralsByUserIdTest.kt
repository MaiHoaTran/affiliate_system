package com.assignment.affiliate.usecases.referral

import com.assignment.affiliate.domain.referral.ReferralRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetAllReferralsByUserIdTest {

    private val referralRepository = mockk<ReferralRepository>()
    private val getAllReferralsByUserId = GetAllReferralsByUserId(referralRepository)

    @Test
    fun `should return list of referral DTOs for a given user id`() {
        val userId = 123L
        val referrals = listOf(mockk<ReferralDTO>(), mockk<ReferralDTO>())

        every { referralRepository.getAllReferralDTOsByUserId(userId) } returns referrals

        val result = getAllReferralsByUserId(userId)

        assertEquals(result, result)
        verify { referralRepository.getAllReferralDTOsByUserId(userId) }
    }
}
