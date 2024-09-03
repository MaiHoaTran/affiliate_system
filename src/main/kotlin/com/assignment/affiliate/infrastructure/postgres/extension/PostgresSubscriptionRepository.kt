package com.assignment.affiliate.infrastructure.postgres.extension

import com.assignment.affiliate.domain.subscription.Subscription
import com.assignment.affiliate.domain.subscription.SubscriptionRepository
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.generated.Keys
import org.jooq.generated.Tables.SUBSCRIPTIONS
import org.jooq.generated.tables.records.SubscriptionsRecord
import org.springframework.stereotype.Repository

@Repository
class PostgresSubscriptionRepository(
    private val dslContext: DSLContext
) : SubscriptionRepository {
    override fun findByUserId(userId: Long): Subscription? {
        return dslContext.select(listAllColumns())
            .from(SUBSCRIPTIONS)
            .where(SUBSCRIPTIONS.USER_ID.eq(userId))
            .fetchOneInto(SubscriptionsRecord::class.java)
            ?.toDomain()
    }

    override fun upsert(subscription: Subscription): Subscription {
        val record = subscription.toRecord()

        return dslContext.insertInto(SUBSCRIPTIONS)
            .set(record)
            .onConflictOnConstraint(Keys.SUBSCRIPTIONS_USER_ID_KEY)
            .doUpdate()
            .set(record)
            .returning()
            .fetchOneInto(SubscriptionsRecord::class.java)!!
            .toDomain()
    }

    override fun findAllByUserIdsAndStatus(
        userIds: List<Long>,
        subscriptionStatus: SubscriptionStatus
    ): List<Subscription> {
        return dslContext.select(listAllColumns())
            .from(SUBSCRIPTIONS)
            .where(SUBSCRIPTIONS.USER_ID.`in`(userIds).and(SUBSCRIPTIONS.STATUS.eq(subscriptionStatus.value)))
            .fetchInto(SubscriptionsRecord::class.java)
            .map { it.toDomain() }
    }

    private fun listAllColumns(): List<TableField<SubscriptionsRecord, out Any>> {
        return listOf(
            SUBSCRIPTIONS.ID,
            SUBSCRIPTIONS.USER_ID,
            SUBSCRIPTIONS.STATUS,
            SUBSCRIPTIONS.CREATED_AT,
            SUBSCRIPTIONS.UPDATED_AT
        )
    }
}
