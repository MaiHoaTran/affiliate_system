package com.assignment.affiliate.usecases.affiliate

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import org.springframework.stereotype.Service
import java.time.Clock

/**
 * Use case to create an affiliate with the given userId
 */
@Service
class CreateAffiliate(
    private val clock: Clock,
    private val affiliateRepository: AffiliateRepository,
    private val generateAffiliateCode: GenerateAffiliateCode
) {
    operator fun invoke(userId: Long): Affiliate {
        affiliateRepository.findByUserId(userId)?.let { return it }

        val affiliateCode = generateAffiliateCode()
        val affiliate = Affiliate(userId = userId, affiliateCode = affiliateCode, createdAt = clock.instant())

        return affiliateRepository.save(affiliate)
    }
}
