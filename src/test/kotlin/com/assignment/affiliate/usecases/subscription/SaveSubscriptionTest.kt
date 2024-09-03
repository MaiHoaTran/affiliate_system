package com.assignment.affiliate.usecases.subscription

import com.assignment.affiliate.domain.subscription.Subscription
import com.assignment.affiliate.domain.subscription.SubscriptionRepository
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant

class SaveSubscriptionTest {
    private val clock = mockk<Clock>()
    private val subscriptionRepository = mockk<SubscriptionRepository>()
    private val saveSubscription = SaveSubscription(clock, subscriptionRepository)

    @Test
    fun `should update existing subscription`() {
        val userId = 1L
        val newStatus = SubscriptionStatus.ACTIVE
        val now = Instant.now()

        val existingSubscription = Subscription(
            userId = userId,
            status = SubscriptionStatus.PENDING,
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600)
        )

        val updatedSubscription = existingSubscription.copy(status = newStatus, updatedAt = now)

        every { clock.instant() } returns now
        every { subscriptionRepository.findByUserId(userId) } returns existingSubscription
        every { subscriptionRepository.upsert(updatedSubscription) } returns updatedSubscription

        val result = saveSubscription(userId, newStatus)

        verify { subscriptionRepository.findByUserId(userId) }
        verify { subscriptionRepository.upsert(updatedSubscription) }
        assertEquals(updatedSubscription, result)
    }

    @Test
    fun `should create new subscription when none exists`() {
        val userId = 1L
        val status = SubscriptionStatus.ACTIVE
        val now = Instant.now()

        val newSubscription = Subscription(
            userId = userId,
            status = status,
            createdAt = now,
            updatedAt = now
        )

        every { clock.instant() } returns now
        every { subscriptionRepository.findByUserId(userId) } returns null
        every { subscriptionRepository.upsert(newSubscription) } returns newSubscription

        val result = saveSubscription(userId, status)

        verify { subscriptionRepository.findByUserId(userId) }
        verify { subscriptionRepository.upsert(newSubscription) }
        assertEquals(newSubscription, result)
    }
}
