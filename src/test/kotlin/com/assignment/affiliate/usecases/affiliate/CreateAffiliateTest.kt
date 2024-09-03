package com.assignment.affiliate.usecases.affiliate

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class CreateAffiliateTest {
    private val clock = Clock.fixed(Instant.parse("2024-09-02T01:00:00.00Z"), ZoneOffset.UTC)
    private val affiliateRepository = mockk<AffiliateRepository>()
    private val generateAffiliateCode = mockk<GenerateAffiliateCode>()
    private val createAffiliate = CreateAffiliate(clock, affiliateRepository, generateAffiliateCode)

    @Test
    fun `should return existing affiliate if it already exists`() {
        val userId = 1L
        val existingAffiliate = Affiliate(userId = userId, affiliateCode = "ABC123", createdAt = clock.instant())
        every { affiliateRepository.findByUserId(userId) } returns existingAffiliate

        val result = createAffiliate(userId)

        assertEquals(existingAffiliate, result)
        verify { affiliateRepository.findByUserId(userId) }
        verify(exactly = 0) { affiliateRepository.save(any()) }
        verify(exactly = 0) { generateAffiliateCode.invoke() }
    }

    @Test
    fun `should create and save new affiliate if it does not exist`() {
        val userId = 1L
        val newAffiliateCode = "XYZ789"
        val newAffiliate = Affiliate(userId = userId, affiliateCode = newAffiliateCode, createdAt = clock.instant())

        every { generateAffiliateCode.invoke() } returns newAffiliateCode
        every { affiliateRepository.findByUserId(userId) } returns null
        every { affiliateRepository.save(any()) } returns newAffiliate

        val result = createAffiliate(userId)

        assertEquals(newAffiliate, result)
        verify { affiliateRepository.findByUserId(userId) }
        verify { affiliateRepository.save(newAffiliate) }
        verify { generateAffiliateCode.invoke() }
    }
}
