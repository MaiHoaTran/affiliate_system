package com.assignment.affiliate.usecases.commission

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.exception.CommissionNotFoundException
import org.springframework.stereotype.Service
import java.time.Clock

/**
 * Use case to update commission status by given commission id
 */
@Service
class UpdateCommissionStatusByCommissionId(
    private val clock: Clock,
    private val commissionRepository: CommissionRepository
) {
    /**
     * @param commissionId the commission id
     * @param status the status to be updated
     */
    operator fun invoke(commissionId: Long, status: CommissionStatus) {
        val commission = commissionRepository.findById(commissionId)
            ?: throw CommissionNotFoundException("Can't find commission by id $commissionId")

        val updatedCommission = commission.copy(
            status = status,
            updatedAt = clock.instant()
        )
        commissionRepository.upsert(updatedCommission)
    }
}
