package com.assignment.affiliate.infrastructure.postgres.extension

import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.toCommissionStatus
import org.jooq.generated.tables.records.CommissionsRecord

fun Commission.toRecord(): CommissionsRecord {
    val record = CommissionsRecord()

    this.id?.let { record.id = it }
    record.referralId = this.referralId
    record.amount = this.amount
    record.status = this.status.value
    this.createdAt?.let { record.createdAt = it }
    this.updatedAt?.let { record.updatedAt = it }

    return record
}

fun CommissionsRecord.toDomain(): Commission {
    return Commission(
        id = this.id,
        referralId = this.referralId,
        amount = this.amount,
        status = this.status.toCommissionStatus(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
