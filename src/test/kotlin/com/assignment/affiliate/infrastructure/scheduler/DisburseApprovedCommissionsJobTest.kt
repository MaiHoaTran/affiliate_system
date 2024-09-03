package com.assignment.affiliate.infrastructure.scheduler

import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.commission.PayoutCommission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.quartz.JobExecutionContext

@ExtendWith(MockKExtension::class)
class DisburseApprovedCommissionsJobTest {
    @MockK(relaxed = true)
    private lateinit var commissionRepository: CommissionRepository

    @SpyK
    @InjectMockKs
    private lateinit var disburseApprovedCommissionsJob: DisburseApprovedCommissionsJob

    @BeforeEach
    fun setUp() {
        disburseApprovedCommissionsJob = spyk(DisburseApprovedCommissionsJob(commissionRepository))
    }

    @Test
    fun `execute should do nothing when there are no commissions to payout`() {
        every { commissionRepository.findAllToPayout() } returns emptyList()

        val jobContext = mockk<JobExecutionContext>()
        disburseApprovedCommissionsJob.execute(jobContext)

        verify { commissionRepository.findAllToPayout() }
    }

    @Test
    fun `execute should update status to PAID when payout is successful`() {
        val payoutCommissions = listOf(
            PayoutCommission(userEmail = "email1@test.com", commissionId = 1, commissionAmount = 19.0),
            PayoutCommission(userEmail = "email2@test.com", commissionId = 2, commissionAmount = 19.0)
        )
        every { commissionRepository.findAllToPayout() } returns payoutCommissions

        val jobContext = mockk<JobExecutionContext>()
        disburseApprovedCommissionsJob.execute(jobContext)

        verify { commissionRepository.findAllToPayout() }
        verify {
            commissionRepository.updateStatus(
                match { it.size == 2 && it.containsAll(setOf(1, 2)) },
                eq(CommissionStatus.PAID)
            )
        }
    }

    @Test
    fun `execute should update status to FAILED when payout fails`() {
        val payoutCommissions = listOf(
            PayoutCommission(userEmail = "email1@test.com", commissionId = 1, commissionAmount = 19.0),
            PayoutCommission(userEmail = "email2@test.com", commissionId = 2, commissionAmount = 19.0)
        )
        every { commissionRepository.findAllToPayout() } returns payoutCommissions
        every { disburseApprovedCommissionsJob.payout(any()) } throws RuntimeException("Payout failed")

        disburseApprovedCommissionsJob.execute(null)

        verify {
            commissionRepository.updateStatus(
                match { it.size == 2 && it.containsAll(setOf(1, 2)) },
                eq(CommissionStatus.FAILED)
            )
        }
    }
}
