package com.assignment.affiliate.infrastructure.scheduler

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.subscription.Subscription
import com.assignment.affiliate.domain.subscription.SubscriptionRepository
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import com.assignment.affiliate.usecases.commission.CommissionDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.quartz.JobExecutionContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@ExtendWith(SpringExtension::class)
class HandlePendingCommissionsJobTest {
    private lateinit var job: HandlePendingCommissionsJob

    private val clock: Clock = Clock.fixed(Instant.parse("2024-09-01T00:00:00Z"), ZoneId.systemDefault())
    private val subscriptionRepository: SubscriptionRepository = mockk(relaxed = true)
    private val commissionRepository: CommissionRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        job = HandlePendingCommissionsJob(clock, subscriptionRepository, commissionRepository)
    }

    @Test
    fun `should approve and reject commissions correctly`() {
        val pendingCommissions = listOf(
            CommissionDTO(
                commissionId = 1,
                affiliateUserId = 1,
                referredUserId = 1,
                referredUserEmail = "user1@test.com",
                commissionStatus = CommissionStatus.PENDING,
                commissionAmount = 19.0
            ),
            CommissionDTO(
                commissionId = 2,
                affiliateUserId = 2,
                referredUserId = 2,
                referredUserEmail = "user2@test.com",
                commissionStatus = CommissionStatus.PENDING,
                commissionAmount = 19.0
            ),
            CommissionDTO(
                commissionId = 3,
                affiliateUserId = 2,
                referredUserId = 3,
                referredUserEmail = "user3@test.com",
                commissionStatus = CommissionStatus.PENDING,
                commissionAmount = 19.0
            )
        )
        val activeSubscriptions = listOf(
            Subscription(id = 1, userId = 1, status = SubscriptionStatus.ACTIVE),
            Subscription(id = 2, userId = 2, status = SubscriptionStatus.ACTIVE)
        )

        every {
            commissionRepository.getAllCommissionDTOsByStatusAndCreatedBeforeOrAt(
                any(),
                any()
            )
        } returns pendingCommissions
        every { subscriptionRepository.findAllByUserIdsAndStatus(any(), any()) } returns activeSubscriptions

        val jobContext = mockk<JobExecutionContext>()
        job.execute(jobContext)

        verify {
            commissionRepository.updateStatus(
                match { it.size == 2 && it.containsAll(setOf(1, 2)) },
                eq(CommissionStatus.APPROVED)
            )
        }
        verify {
            commissionRepository.updateStatus(
                match { it.size == 1 && it.containsAll(setOf(3)) },
                eq(CommissionStatus.REJECTED)
            )
        }
    }

    @Test
    fun `should not update if no commissions found`() {
        every {
            commissionRepository.getAllCommissionDTOsByStatusAndCreatedBeforeOrAt(
                any(),
                any()
            )
        } returns emptyList()

        val jobContext = mockk<JobExecutionContext>()
        job.execute(jobContext)

        verify(exactly = 0) {
            commissionRepository.updateStatus(any(), any())
        }
    }
}
