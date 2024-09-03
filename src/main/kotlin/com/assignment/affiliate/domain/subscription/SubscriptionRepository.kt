package com.assignment.affiliate.domain.subscription

/**
 * Subscription repository performs data operations related to subscriptions table
 */
interface SubscriptionRepository {
    /**
     * Find subscription by user id
     *
     * @param userId the user id
     * @return [Subscription] or null
     */
    fun findByUserId(userId: Long): Subscription?

    /**
     * Insert/Update the given subscription
     *
     * @param subscription the subscription to be inserted/updated
     * @return [Subscription]
     */
    fun upsert(subscription: Subscription): Subscription

    /**
     * Find all subscription by given list of user ids and subscription status
     *
     * @param userIds the list of user id
     * @param subscriptionStatus the subscription status
     * @return list of [Subscription]
     */
    fun findAllByUserIdsAndStatus(userIds: List<Long>, subscriptionStatus: SubscriptionStatus): List<Subscription>
}
