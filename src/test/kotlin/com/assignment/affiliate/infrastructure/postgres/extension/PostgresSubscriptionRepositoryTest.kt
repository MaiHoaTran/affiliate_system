package com.assignment.affiliate.infrastructure.postgres.extension

import com.assignment.affiliate.domain.subscription.Subscription
import com.assignment.affiliate.domain.subscription.SubscriptionRepository
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import com.assignment.affiliate.infrastructure.postgres.BasePostgresTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PostgresSubscriptionRepositoryTest : BasePostgresTest() {
    private lateinit var subscriptionRepository: SubscriptionRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        subscriptionRepository = PostgresSubscriptionRepository(dslContext)
    }

    @Test
    fun `findByUserId - there is no subscription - return null`() {
        assertThat(subscriptionRepository.findByUserId(100)).isNull()
    }

    @Test
    fun `findByUserId - there are some subscriptions - return found one`() {
        val user = createUser(email = "email@test.com")
        val subscription = createSubscription(userId = user.id!!, status = SubscriptionStatus.SUSPENDED)

        assertThat(subscriptionRepository.findByUserId(user.id!!)).isEqualTo(subscription)
    }

    @Test
    fun `findAllByUserIdsAndStatus - there is no subscription - return empty`() {
        assertThat(
            subscriptionRepository.findAllByUserIdsAndStatus(
                listOf(100),
                SubscriptionStatus.SUSPENDED
            )
        ).isEmpty()
    }

    @Test
    fun `findAllByUserIdsAndStatus - there are some subscriptions - return satisfied subscriptions`() {
        val user1 = createUser(email = "email1@test.com")
        val user2 = createUser(email = "email2@test.com")
        val user3 = createUser(email = "email3@test.com")
        createSubscription(userId = user1.id!!, status = SubscriptionStatus.SUSPENDED)
        val subscription2 = createSubscription(userId = user2.id!!, status = SubscriptionStatus.PENDING)
        createSubscription(userId = user3.id!!, status = SubscriptionStatus.ACTIVE)

        assertThat(
            subscriptionRepository.findAllByUserIdsAndStatus(
                listOf(user1.id!!, user2.id!!, user3.id!!),
                SubscriptionStatus.PENDING
            )
        )
            .isEqualTo(listOf(subscription2))
    }

    @Test
    fun `upsert - there is no subscription - create new subscription `() {
        val user = createUser(email = "email@test.com")
        val subscription = Subscription(
            id = 1,
            userId = user.id!!,
            status = SubscriptionStatus.SUSPENDED,
            createdAt = clock.instant(),
            updatedAt = clock.instant()
        )

        assertThat(subscriptionRepository.upsert(subscription))
            .isEqualTo(subscription)
    }

    @Test
    fun `upsert - the subscription is existed - update this subscription `() {
        val user = createUser(email = "email@test.com")
        val existingSubscription = createSubscription(userId = user.id!!, status = SubscriptionStatus.SUSPENDED)
        val subscription = existingSubscription.copy(status = SubscriptionStatus.CANCELED, updatedAt = clock.instant())

        assertThat(subscriptionRepository.upsert(subscription))
            .isEqualTo(subscription)
    }
}
