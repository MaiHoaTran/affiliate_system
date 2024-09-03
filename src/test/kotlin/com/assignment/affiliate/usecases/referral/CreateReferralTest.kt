package com.assignment.affiliate.usecases.referral

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import com.assignment.affiliate.domain.exception.AffiliateCodeNotFoundException
import com.assignment.affiliate.domain.referral.Referral
import com.assignment.affiliate.domain.referral.ReferralRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateReferralTest {

    private val affiliateRepository = mockk<AffiliateRepository>()
    private val referralRepository = mockk<ReferralRepository>()
    private val createReferral = CreateReferral(affiliateRepository, referralRepository)

    @Test
    fun `should create referral when referral code is valid`() {
        val userId = 123L
        val refCode = "VALID_CODE"
        val affiliate = Affiliate(id = 1, affiliateCode = refCode, userId = userId)
        val referral = Referral(affiliateId = affiliate.id!!, referredUserId = userId)

        every { affiliateRepository.findByAffiliateCode(refCode) } returns affiliate
        every { referralRepository.save(referral) } returns referral

        createReferral(userId, refCode)

        verify { affiliateRepository.findByAffiliateCode(refCode) }
        verify { referralRepository.save(referral) }
    }

    @Test
    fun `should not create referral when referral code is null`() {
        val userId = 123L
        val refCode: String? = null

        createReferral(userId, refCode)

        verify(exactly = 0) { affiliateRepository.findByAffiliateCode(any()) }
        verify(exactly = 0) { referralRepository.save(any()) }
    }

    @Test
    fun `should throw exception when referral code is invalid`() {
        val userId = 123L
        val refCode = "INVALID_CODE"

        every { affiliateRepository.findByAffiliateCode(refCode) } returns null

        assertThrows<AffiliateCodeNotFoundException> {
            createReferral(userId, refCode)
        }

        verify { affiliateRepository.findByAffiliateCode(refCode) }
        verify(exactly = 0) { referralRepository.save(any()) }
    }
}
