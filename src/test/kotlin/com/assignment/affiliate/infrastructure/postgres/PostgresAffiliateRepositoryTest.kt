package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import com.assignment.affiliate.domain.exception.DuplicateAffiliateCodeException
import com.assignment.affiliate.domain.exception.DuplicateAffiliateUserException
import com.assignment.affiliate.usecases.affiliate.AffiliateDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class PostgresAffiliateRepositoryTest : BasePostgresTest() {
    private lateinit var affiliateRepository: AffiliateRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        this.affiliateRepository = PostgresAffiliateRepository(dslContext)
    }

    @Test
    fun `findAll - there is no affiliates - return empty`() {
        assertThat(affiliateRepository.findAll()).isEmpty()
    }

    @Test
    fun `findAll - return all affiliates`() {
        val user1 = createUser(email = "email.1@test.com")
        val user2 = createUser(email = "email.2@test.com")
        val affiliate1 = createAffiliate(userId = user1.id!!, affiliateCode = "0123456789")
        val affiliate2 = createAffiliate(userId = user2.id!!, affiliateCode = "1234567890")

        assertThat(affiliateRepository.findAll())
            .isEqualTo(
                listOf(
                    AffiliateDTO(
                        affiliateId = affiliate1.id!!,
                        affiliateCode = affiliate1.affiliateCode,
                        affiliateUserId = user1.id!!,
                        affiliateUserEmail = user1.email
                    ),
                    AffiliateDTO(
                        affiliateId = affiliate2.id!!,
                        affiliateCode = affiliate2.affiliateCode,
                        affiliateUserId = user2.id!!,
                        affiliateUserEmail = user2.email
                    )
                )
            )
    }

    @Test
    fun `findByUserId - there is no affiliates - return null`() {
        assertThat(affiliateRepository.findByUserId(1))
            .isNull()
    }

    @Test
    fun `findByUserId - return found one`() {
        val now = Instant.now()
        val user1 = createUser(email = "email.1@test.com")
        val user2 = createUser(email = "email.2@test.com")
        createAffiliate(userId = user1.id!!, affiliateCode = "0123456789")
        val affiliate2 = createAffiliate(userId = user2.id!!, affiliateCode = "1234567890", createdAt = now)

        assertThat(affiliateRepository.findByUserId(user2.id!!))
            .isEqualTo(
                Affiliate(
                    id = affiliate2.id!!,
                    affiliateCode = affiliate2.affiliateCode,
                    userId = user2.id!!,
                    createdAt = now
                )
            )
    }

    @Test
    fun `findByAffiliateCode - there is no affiliates - return null`() {
        assertThat(affiliateRepository.findByAffiliateCode("1234567890"))
            .isNull()
    }

    @Test
    fun `findByAffiliateCode - return found one`() {
        val now = Instant.now()
        val user1 = createUser(email = "email.1@test.com")
        val user2 = createUser(email = "email.2@test.com")
        createAffiliate(userId = user1.id!!, affiliateCode = "0123456789")
        val affiliate2 = createAffiliate(userId = user2.id!!, affiliateCode = "1234567890", createdAt = now)

        assertThat(affiliateRepository.findByAffiliateCode(affiliate2.affiliateCode))
            .isEqualTo(
                Affiliate(
                    id = affiliate2.id!!,
                    affiliateCode = affiliate2.affiliateCode,
                    userId = user2.id!!,
                    createdAt = now
                )
            )
    }

    @Test
    fun `save - there is no affiliate - save successfully`() {
        val user = createUser(email = "email@test.com")
        val now = Instant.now()
        val affiliate = Affiliate(
            affiliateCode = "0123456789",
            userId = user.id!!,
            createdAt = now
        )

        assertThat(affiliateRepository.save(affiliate))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(affiliate)
    }

    @Test
    fun `save - there is affiliate with the same code - save failed`() {
        val user1 = createUser(email = "email.1@test.com")
        val user2 = createUser(email = "email.2@test.com")
        val affiliateCode = "0123456789"
        createAffiliate(userId = user2.id!!, affiliateCode = affiliateCode)

        val affiliate = Affiliate(
            affiliateCode = affiliateCode,
            userId = user1.id!!,
            createdAt = Instant.now()
        )

        assertThrows<DuplicateAffiliateCodeException> {
            affiliateRepository.save(affiliate)
        }
    }

    @Test
    fun `save - there is affiliate with the same userId - save failed`() {
        val user = createUser(email = "email@test.com")
        createAffiliate(userId = user.id!!, affiliateCode = "0987654321")

        val affiliate = Affiliate(
            affiliateCode = "14567890",
            userId = user.id!!,
            createdAt = Instant.now()
        )

        assertThrows<DuplicateAffiliateUserException> {
            affiliateRepository.save(affiliate)
        }
    }
}
