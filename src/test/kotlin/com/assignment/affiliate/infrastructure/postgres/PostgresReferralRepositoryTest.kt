package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.referral.ReferralRepository
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.usecases.referral.ReferralDTO
import com.assignment.affiliate.usecases.referral.ReferralStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PostgresReferralRepositoryTest : BasePostgresTest() {
    private lateinit var referralRepository: ReferralRepository

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User
    private lateinit var affiliate1: Affiliate
    private lateinit var affiliate2: Affiliate
    private lateinit var affiliate3: Affiliate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        referralRepository = PostgresReferralRepository(dslContext)

        createUsers()
        createAffiliates()
    }

    @Test
    fun `findByReferredUserId - there is no referral - return null`() {
        assertThat(referralRepository.findByReferredUserId(100)).isNull()
    }

    @Test
    fun `findByReferredUserId - there are referrals - return found one`() {
        val referral1 = createReferral(id = 1, affiliateId = affiliate1.id!!, referredUserId = user2.id!!)
        createReferral(id = 2, affiliateId = affiliate2.id!!, referredUserId = user3.id!!)

        assertThat(referralRepository.findByReferredUserId(user2.id!!))
            .isEqualTo(referral1)
    }

    @Test
    fun `findAllByAffiliateId - there is no referral - return empty`() {
        assertThat(referralRepository.findAllByAffiliateId(100)).isEmpty()
    }

    @Test
    fun `findAllByAffiliateId - there are referrals - return satisfied affiliates`() {
        createReferral(id = 1, affiliateId = affiliate1.id!!, referredUserId = user2.id!!)
        val referral2 = createReferral(id = 2, affiliateId = affiliate2.id!!, referredUserId = user3.id!!)

        assertThat(referralRepository.findAllByAffiliateId(affiliate2.id!!))
            .isEqualTo(listOf(referral2))
    }

    @Test
    fun `getAllReferralDTOsByUserId - there is no commission - return empty`() {
        assertThat(referralRepository.getAllReferralDTOsByUserId(100)).isEmpty()
    }

    @Test
    fun `getAllReferralDTOsByUserId - there are commissions - return satisfied commissions`() {
        val user4 = createUser(id = 4, email = "email.4@test.com")
        val user5 = createUser(id = 5, email = "email.5@test.com")

        val referral1 = createReferral(id = 1, affiliateId = affiliate1.id!!, referredUserId = user2.id!!)
        val referral2 = createReferral(id = 2, affiliateId = affiliate2.id!!, referredUserId = user3.id!!)
        val referral3 = createReferral(id = 4, affiliateId = affiliate2.id!!, referredUserId = user4.id!!)
        val referral4 = createReferral(id = 5, affiliateId = affiliate2.id!!, referredUserId = user5.id!!)

        createCommission(referralId = referral1.id!!, status = CommissionStatus.APPROVED)
        createCommission(referralId = referral2.id!!, status = CommissionStatus.PENDING)
        createCommission(referralId = referral3.id!!, status = CommissionStatus.APPROVED)

        assertThat(referralRepository.getAllReferralDTOsByUserId(user2.id!!))
            .isEqualTo(
                listOf(
                    ReferralDTO(
                        referralId = referral2.id!!,
                        referralStatus = ReferralStatus.CONVERTED,
                        referredUserId = user3.id!!,
                        referredUserEmail = user3.email,
                        commissionAmount = Commission.DEFAULT_COMMISSION_AMOUNT
                    ),
                    ReferralDTO(
                        referralId = referral3.id!!,
                        referralStatus = ReferralStatus.CONVERTED,
                        referredUserId = user4.id!!,
                        referredUserEmail = user4.email,
                        commissionAmount = Commission.DEFAULT_COMMISSION_AMOUNT
                    ),
                    ReferralDTO(
                        referralId = referral4.id!!,
                        referralStatus = ReferralStatus.PENDING,
                        referredUserId = user5.id!!,
                        referredUserEmail = user5.email,
                        commissionAmount = null
                    )
                )
            )
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
}
