package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import org.springframework.stereotype.Service

/**
 * Use case to calculate the total amount of commissions by given user id and commission status
 */
@Service
class CalculateCommissionAmountByUserIdAndStatus(
    private val commissionRepository: CommissionRepository
) {
    /**
     * @param userId the user id
     * @param status the commission status
     *
     * @return the total amount of commissions by given user id and commission status
     */
    operator fun invoke(userId: Long, status: CommissionStatus): Double {
        return commissionRepository.sumByUserIdAndStatus(userId, status)
    }
}
