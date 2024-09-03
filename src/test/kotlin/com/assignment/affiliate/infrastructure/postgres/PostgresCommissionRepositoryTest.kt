package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.commission.PayoutCommission
import com.assignment.affiliate.domain.referral.Referral
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.usecases.commission.CommissionDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertNull

@PostgresTest
class PostgresCommissionRepositoryTest : BasePostgresTest() {
    private lateinit var commissionRepository: CommissionRepository

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User
    private lateinit var affiliate1: Affiliate
    private lateinit var affiliate2: Affiliate
    private lateinit var affiliate3: Affiliate
    private lateinit var referral1: Referral
    private lateinit var referral2: Referral

    @BeforeEach
    override fun setUp() {
        super.setUp()

        this.commissionRepository = PostgresCommissionRepository(clock, dslContext)

        createUsers()
        createAffiliates()
        createReferrals()
    }

    @Test
    fun `findById - there is no commission - return null`() {
        assertNull(commissionRepository.findById(1))
    }

    @Test
    fun `findById - there are commissions - return found one`() {
        val commission1 = createCommission(referralId = referral1.id!!)
        createCommission(referralId = referral2.id!!)

        assertThat(commissionRepository.findById(commission1.id!!))
            .isEqualTo(commission1)
    }

    @Test
    fun `findByReferralId - there is no commission - return null`() {
        assertNull(commissionRepository.findByReferralId(100))
    }

    @Test
    fun `findByReferralId - there are commissions - return found one`() {
        val referralId1 = referral1.id!!
        val commission1 = createCommission(referralId = referralId1)
        createCommission(referralId = referral2.id!!)

        assertThat(commissionRepository.findByReferralId(referralId1))
            .isEqualTo(commission1)
    }

    @Test
    fun `findAllByAffiliateId - there is no commission - return empty`() {
        assertThat(commissionRepository.findAllByAffiliateId(100)).isEmpty()
    }

    @Test
    fun `findAllByAffiliateId - there are commissions - return satisfied commissions`() {
        val commission1 = createCommission(referralId = referral1.id!!)
        createCommission(referralId = referral2.id!!)

        assertThat(commissionRepository.findAllByAffiliateId(affiliate1.id!!))
            .isEqualTo(listOf(commission1))
    }

    @Test
    fun `findAllByStatus - there is no commission - return empty`() {
        assertThat(commissionRepository.findAllByStatus(CommissionStatus.PENDING)).isEmpty()
    }

    @Test
    fun `findAllByStatus - there are commissions - return satisfied commissions`() {
        val commission1 = createCommission(referralId = referral1.id!!, status = CommissionStatus.PENDING)
        val commission2 = createCommission(referralId = referral2.id!!, status = CommissionStatus.PENDING)

        assertThat(commissionRepository.findAllByStatus(CommissionStatus.PENDING))
            .isEqualTo(listOf(commission1, commission2))
        assertThat(commissionRepository.findAllByStatus(CommissionStatus.APPROVED))
            .isEmpty()
    }

    @Test
    fun `getAllCommissionDTOsByStatusAndCreatedBeforeOrAt - there is no commission - return empty`() {
        assertThat(
            commissionRepository.getAllCommissionDTOsByStatusAndCreatedBeforeOrAt(
                CommissionStatus.PENDING,
                clock.instant()
            )
        ).isEmpty()
    }

    @Test
    fun `getAllCommissionDTOsByStatusAndCreatedBeforeOrAt - there are commissions - return satisfied commissions`() {
        val now = clock.instant()
        val commission1 = createCommission(
            referralId = referral1.id!!,
            status = CommissionStatus.PENDING,
            createdAt = now.minusSeconds(3)
        )
        createCommission(referralId = referral2.id!!, status = CommissionStatus.PENDING, createdAt = now)

        assertThat(
            commissionRepository.getAllCommissionDTOsByStatusAndCreatedBeforeOrAt(
                CommissionStatus.PENDING,
                now.minusSeconds(2)
            )
        )
            .isEqualTo(
                listOf(
                    CommissionDTO(
                        commissionId = commission1.id!!,
                        commissionStatus = commission1.status,
                        commissionAmount = commission1.amount,
                        affiliateUserId = user1.id!!,
                        referredUserId = user2.id!!,
                        referredUserEmail = user2.email
                    )
                )
            )
    }

    @Test
    fun `findAllByUserId - there is no commission - return empty`() {
        assertThat(commissionRepository.findAllByUserId(100)).isEmpty()
    }

    @Test
    fun `findAllByUserId - there are commissions - return satisfied commissions`() {
        createCommission(referralId = referral1.id!!)
        val commission2 = createCommission(referralId = referral2.id!!)

        assertThat(commissionRepository.findAllByUserId(user2.id!!))
            .isEqualTo(listOf(commission2))
    }

    @Test
    fun `findAllToPayout - there is no commission - return empty`() {
        assertThat(commissionRepository.findAllToPayout()).isEmpty()
    }

    @Test
    fun `findAllToPayout - there are commissions - return satisfied commissions`() {
        createCommission(referralId = referral1.id!!, status = CommissionStatus.PENDING)
        val commission2 = createCommission(referralId = referral2.id!!, status = CommissionStatus.APPROVED)

        assertThat(commissionRepository.findAllToPayout())
            .isEqualTo(
                listOf(
                    PayoutCommission(
                        userEmail = user2.email,
                        commissionId = commission2.id!!,
                        commissionAmount = commission2.amount
                    )
                )
            )
    }

    @Test
    fun `getAllCommissionDTOsByAffiliateId - there is no commission - return empty`() {
        assertThat(commissionRepository.getAllCommissionDTOsByAffiliateId(100)).isEmpty()
    }

    @Test
    fun `getAllCommissionDTOsByAffiliateId - there are commissions - return satisfied commissions`() {
        createCommission(referralId = referral1.id!!, status = CommissionStatus.PENDING)
        val commission2 = createCommission(referralId = referral2.id!!, status = CommissionStatus.APPROVED)

        assertThat(commissionRepository.getAllCommissionDTOsByAffiliateId(affiliate2.id!!))
            .isEqualTo(
                listOf(
                    CommissionDTO(
                        commissionId = commission2.id!!,
                        commissionStatus = commission2.status,
                        commissionAmount = commission2.amount,
                        affiliateUserId = user2.id!!,
                        referredUserId = user3.id!!,
                        referredUserEmail = user3.email
                    )
                )
            )
    }

    @Test
    fun `upsert - there is no commission - insert new commission`() {
        val commission = Commission(
            referralId = referral1.id!!,
            status = CommissionStatus.PENDING,
            amount = Commission.DEFAULT_COMMISSION_AMOUNT,
            createdAt = clock.instant(),
            updatedAt = clock.instant()
        )

        assertThat(commissionRepository.upsert(commission))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(commission)
    }

    @Test
    fun `upsert - there is existing commission - update commission`() {
        val existingCommissions = createCommission(referralId = referral1.id!!, status = CommissionStatus.PENDING)
        val updatedCommission = existingCommissions.copy(status = CommissionStatus.APPROVED, updatedAt = Instant.now())

        assertThat(commissionRepository.upsert(updatedCommission))
            .isEqualTo(updatedCommission)
    }

    @Test
    fun `updateStatus - there is no commission - do nothing`() {
        commissionRepository.updateStatus(listOf(1, 2), CommissionStatus.PENDING)
    }

    @Test
    fun `updateStatus - there are commissions - update commissions`() {
        val commission1 = createCommission(referralId = referral1.id!!, status = CommissionStatus.PENDING)
        val commission2 = createCommission(referralId = referral2.id!!, status = CommissionStatus.PENDING)
        assertThat(commissionRepository.findAllByStatus(CommissionStatus.PENDING))
            .hasSize(2)

        commissionRepository.updateStatus(
            listOf(commission1.id!!, commission2.id!!),
            CommissionStatus.APPROVED
        )

        assertThat(commissionRepository.findAllByStatus(CommissionStatus.PENDING))
            .isEmpty()
        assertThat(commissionRepository.findAllByStatus(CommissionStatus.APPROVED))
            .hasSize(2)
    }

    @Test
    fun `sumByUserIdAndStatus - find all commissions by user id and status - sum commission amount`() {
        val user4 = createUser(id = 4, email = "email.4@test.com")
        val referral3 = createReferral(id = 4, affiliateId = affiliate2.id!!, referredUserId = user4.id!!)

        createCommission(referralId = referral1.id!!, status = CommissionStatus.APPROVED)
        createCommission(referralId = referral2.id!!, status = CommissionStatus.APPROVED)
        createCommission(referralId = referral3.id!!, status = CommissionStatus.APPROVED)

        assertThat(commissionRepository.sumByUserIdAndStatus(user1.id!!, CommissionStatus.APPROVED))
            .isEqualTo(19.0)
        assertThat(commissionRepository.sumByUserIdAndStatus(user1.id!!, CommissionStatus.PENDING))
            .isEqualTo(0.0)
        assertThat(commissionRepository.sumByUserIdAndStatus(user2.id!!, CommissionStatus.APPROVED))
            .isEqualTo(38.0)
        assertThat(commissionRepository.sumByUserIdAndStatus(user3.id!!, CommissionStatus.APPROVED))
            .isEqualTo(0.0)
    }

    private fun createUsers() {
        user1 = createUser(id = 1, email = "email.1@test.com")
        user2 = createUser(id = 2, email = "email.2@test.com")
        user3 = createUser(id = 3, email = "email.3@test.com")
    }

    private fun createAffiliates() {
        affiliate1 = createAffiliate(id = 1, userId = user1.id!!, affiliateCode = generateAffiliateCode())
        affiliate2 = createAffiliate(id = 2, userId = user2.id!!, affiliateCode = generateAffiliateCode())
        affiliate3 = createAffiliate(id = 3, userId = user3.id!!, affiliateCode = generateAffiliateCode())
    }

    private fun createReferrals() {
        referral1 = createReferral(id = 1, affiliateId = affiliate1.id!!, referredUserId = user2.id!!)
        referral2 = createReferral(id = 2, affiliateId = affiliate2.id!!, referredUserId = user3.id!!)
    }
}
