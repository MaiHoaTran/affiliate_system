package com.assignment.affiliate.infrastructure.scheduler

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.subscription.SubscriptionRepository
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.temporal.ChronoUnit

/**
 * Job to automatically approve/reject pending commissions:
 * - If after 14 days, the user does not cancel subscriptions, update these commissions status to approved
 * - Otherwise, update these commissions status to rejected.
 * This job will be run every day at 0:00 UTC
 */
@Component
@Transactional
class HandlePendingCommissionsJob(
    private val clock: Clock,
    private val subscriptionRepository: SubscriptionRepository,
    private val commissionRepository: CommissionRepository
) : Job {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun execute(context: JobExecutionContext) {
        logger.info("Start approve/reject pending commissions")

        val commissions = commissionRepository.getAllCommissionDTOsByStatusAndCreatedBeforeOrAt(
            CommissionStatus.PENDING,
            clock.instant().plus(14, ChronoUnit.DAYS)
        )

        if (commissions.isEmpty()) {
            logger.info("There is no pending commissions which are created before or equals to 14 days")
            return
        }

        val checkingUserIds = commissions.flatMap { setOf(it.affiliateUserId, it.referredUserId) }
        val activatedUserIds =
            subscriptionRepository.findAllByUserIdsAndStatus(checkingUserIds, SubscriptionStatus.ACTIVE)
                .map { it.userId }

        val commissionIdsToBeApproved = commissions
            .filter { activatedUserIds.contains(it.referredUserId) && activatedUserIds.contains(it.affiliateUserId) }
            .map { it.commissionId }
        val commissionIdsToBeRejected = commissions.map { it.commissionId } - commissionIdsToBeApproved.toSet()

        commissionIdsToBeApproved.takeIf { it.isNotEmpty() }?.let {
            commissionRepository.updateStatus(it, CommissionStatus.APPROVED)
        }
        commissionIdsToBeRejected.takeIf { it.isNotEmpty() }?.let {
            commissionRepository.updateStatus(commissionIdsToBeRejected, CommissionStatus.REJECTED)
        }

        logger.info("Finished approve/reject pending commissions")
    }
}
