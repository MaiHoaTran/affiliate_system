package com.assignment.affiliate.infrastructure.scheduler

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.commission.PayoutCommission
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Job to disburse the approved commission. This job will be run every day at 1:00 UTC
 */
@Component
class DisburseApprovedCommissionsJob(
    private val commissionRepository: CommissionRepository
) : Job {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun execute(context: JobExecutionContext?) {
        logger.info("Start disburse for approved commissions")

        val payoutCommissions = commissionRepository.findAllToPayout()
        if (payoutCommissions.isEmpty()) {
            return
        }
        val commissionIds = payoutCommissions.map { it.commissionId }

        try {
            payout(payoutCommissions)

            commissionRepository.updateStatus(commissionIds, CommissionStatus.PAID)
            logger.info("Finished disburse for approved commissions")
        } catch (exception: Exception) {
            logger.error("Failed to disburse for approved commissions", exception)

            commissionRepository.updateStatus(commissionIds, CommissionStatus.FAILED)
            sendFailedNotify()
        }
    }

    private fun sendFailedNotify() {
        // Send notification that process to payout approved commissions are failed
    }

    fun payout(commissions: List<PayoutCommission>) {
        // Implement later
    }
}
