package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import org.springframework.stereotype.Service

/**
 * Use case to update status for all commissions by given affiliate id
 */
@Service
class UpdateCommissionStatusesByAffiliateId(
    private val commissionRepository: CommissionRepository
) {
    /**
     * @param affiliateId the given affiliate id
     * @param status the status to be updated
     */
    operator fun invoke(affiliateId: Long, status: CommissionStatus) {
        commissionRepository.findAllByAffiliateId(affiliateId)
            .map { it.id!! }
            .takeIf { it.isNotEmpty() }
            ?.let {
                commissionRepository.updateStatus(it, status)
            }
    }
}
