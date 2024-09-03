package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.CommissionRepository
import org.springframework.stereotype.Service

/**
 * Use case to get all commission by given affiliate id
 */
@Service
class GetAllCommissionsByAffiliateId(
    private val commissionRepository: CommissionRepository
) {
    /**
     * @param affiliateId the affiliate id
     * @return the list of [CommissionDTO]
     */
    operator fun invoke(affiliateId: Long): List<CommissionDTO> {
        return commissionRepository.getAllCommissionDTOsByAffiliateId(affiliateId)
    }
}
