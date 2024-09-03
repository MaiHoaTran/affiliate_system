package com.assignment.affiliate.usecases.subscription

import com.assignment.affiliate.domain.subscription.Subscription
import com.assignment.affiliate.domain.subscription.SubscriptionRepository
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import org.springframework.stereotype.Service
import java.time.Clock

/**
 * Use case to save subscription with given user id and subscription status.
 * - If the subscription with user id is existed, do update subscription status.
 * - Otherwise, create a new subscription with given properties.
 */
@Service
class SaveSubscription(
    private val clock: Clock,
    private val subscriptionRepository: SubscriptionRepository
) {
    /**
     * @param userId the user id
     * @param subscriptionStatus the subscription status
     * @return [Subscription]
     */
    operator fun invoke(userId: Long, subscriptionStatus: SubscriptionStatus): Subscription {
        val existingSubscription = subscriptionRepository.findByUserId(userId)

        val subscription = existingSubscription?.copy(status = subscriptionStatus, updatedAt = clock.instant())
            ?: Subscription(
                userId = userId,
                status = subscriptionStatus,
                createdAt = clock.instant(),
                updatedAt = clock.instant()
            )

        return subscriptionRepository.upsert(subscription)
    }
}
