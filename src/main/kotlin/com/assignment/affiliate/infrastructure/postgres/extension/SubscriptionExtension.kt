package com.assignment.affiliate.infrastructure.postgres.extension

import com.assignment.affiliate.domain.subscription.Subscription
import com.assignment.affiliate.domain.subscription.toSubscriptionStatus
import org.jooq.generated.tables.records.SubscriptionsRecord

fun Subscription.toRecord(): SubscriptionsRecord {
    val record = SubscriptionsRecord()

    this.id?.let { record.id = it }
    record.userId = this.userId
    record.status = this.status.value
    this.createdAt?.let { record.createdAt = it }
    this.updatedAt?.let { record.updatedAt = it }

    return record
}

fun SubscriptionsRecord.toDomain(): Subscription {
    return Subscription(
        id = this.id,
        userId = this.userId,
        status = this.status.toSubscriptionStatus(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
