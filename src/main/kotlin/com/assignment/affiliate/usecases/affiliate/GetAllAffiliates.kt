package com.assignment.affiliate.usecases.affiliate

import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import org.springframework.stereotype.Service

/**
 * Use case to get all affiliates
 */
@Service
class GetAllAffiliates(
    private val affiliateRepository: AffiliateRepository
) {
    /**
     * @return the list of [AffiliateDTO]
     */
    operator fun invoke(): List<AffiliateDTO> {
        return affiliateRepository.findAll()
    }
}
