package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.CommissionRepository
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.commission.PayoutCommission
import com.assignment.affiliate.domain.commission.toCommissionStatus
import com.assignment.affiliate.infrastructure.postgres.extension.toDomain
import com.assignment.affiliate.infrastructure.postgres.extension.toRecord
import com.assignment.affiliate.usecases.commission.CommissionDTO
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.generated.Keys
import org.jooq.generated.Tables.AFFILIATES
import org.jooq.generated.Tables.COMMISSIONS
import org.jooq.generated.Tables.REFERRALS
import org.jooq.generated.Tables.USERS
import org.jooq.generated.tables.records.CommissionsRecord
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.Instant

@Repository
class PostgresCommissionRepository(
    private val clock: Clock,
    private val dslContext: DSLContext
) : CommissionRepository {
    override fun findById(id: Long): Commission? {
        return dslContext.select(listAllColumns())
            .from(COMMISSIONS)
            .where(COMMISSIONS.ID.eq(id))
            .fetchOneInto(CommissionsRecord::class.java)
            ?.toDomain()
    }

    override fun findByReferralId(referralId: Long): Commission? {
        return dslContext.select(listAllColumns())
            .from(COMMISSIONS)
            .where(COMMISSIONS.REFERRAL_ID.eq(referralId))
            .fetchOneInto(CommissionsRecord::class.java)
            ?.toDomain()
    }

    override fun findAllByStatus(status: CommissionStatus): List<Commission> {
        return dslContext.select(listAllColumns())
            .from(COMMISSIONS)
            .where(COMMISSIONS.STATUS.eq(status.value))
            .fetchInto(CommissionsRecord::class.java)
            .map { it.toDomain() }
    }

    override fun findAllByAffiliateId(affiliateId: Long): List<Commission> {
        return dslContext.select(listAllColumns())
            .from(COMMISSIONS)
            .join(REFERRALS)
            .on(COMMISSIONS.REFERRAL_ID.eq(REFERRALS.ID))
            .where(REFERRALS.AFFILIATE_ID.eq(affiliateId))
            .fetchInto(CommissionsRecord::class.java)
            .map { it.toDomain() }
    }

    override fun findAllByUserId(userId: Long): List<Commission> {
        return dslContext.select(listAllColumns())
            .from(COMMISSIONS)
            .join(REFERRALS)
            .on(COMMISSIONS.REFERRAL_ID.eq(REFERRALS.ID))
            .join(AFFILIATES)
            .on(AFFILIATES.ID.eq(REFERRALS.AFFILIATE_ID))
            .where(AFFILIATES.USER_ID.eq(userId))
            .fetchInto(CommissionsRecord::class.java)
            .map { it.toDomain() }
    }

    override fun findAllToPayout(): List<PayoutCommission> {
        return dslContext.select(
            COMMISSIONS.ID.`as`("commissionId"),
            USERS.EMAIL.`as`("userEmail"),
            COMMISSIONS.AMOUNT.`as`("commissionAmount")
        )
            .from(COMMISSIONS)
            .join(REFERRALS).on(REFERRALS.ID.eq(COMMISSIONS.REFERRAL_ID))
            .join(AFFILIATES).on(AFFILIATES.ID.eq(REFERRALS.AFFILIATE_ID))
            .join(USERS).on(USERS.ID.eq(AFFILIATES.USER_ID))
            .where(COMMISSIONS.STATUS.eq(CommissionStatus.APPROVED.value))
            .fetchInto(PayoutCommission::class.java)
    }

    override fun getAllCommissionDTOsByAffiliateId(affiliateId: Long): List<CommissionDTO> {
        return getAllCommissionDTOs(REFERRALS.AFFILIATE_ID.eq(affiliateId))
    }

    override fun getAllCommissionDTOsByStatusAndCreatedBeforeOrAt(status: CommissionStatus, instant: Instant): List<CommissionDTO> {
        return getAllCommissionDTOs(
            COMMISSIONS.STATUS.eq(status.value)
                .eq(COMMISSIONS.CREATED_AT.lessOrEqual(instant))
        )
    }

    private fun getAllCommissionDTOs(condition: Condition): List<CommissionDTO> {
        return dslContext.select(
            COMMISSIONS.ID.`as`("commissionId"),
            COMMISSIONS.STATUS.`as`("commissionStatus"),
            COMMISSIONS.AMOUNT.`as`("commissionAmount"),
            AFFILIATES.USER_ID.`as`("affiliateUserId"),
            USERS.ID.`as`("referredUserId"),
            USERS.EMAIL.`as`("referredUserEmail")
        )
            .from(COMMISSIONS)
            .join(REFERRALS).on(REFERRALS.ID.eq(COMMISSIONS.REFERRAL_ID))
            .join(AFFILIATES).on(AFFILIATES.ID.eq(REFERRALS.AFFILIATE_ID))
            .join(USERS).on(USERS.ID.eq(REFERRALS.REFERRED_USER_ID))
            .where(condition)
            .fetch { record ->
                CommissionDTO(
                    commissionId = record.getValue("commissionId", Long::class.java),
                    commissionStatus = record.getValue("commissionStatus", String::class.java).toCommissionStatus(),
                    commissionAmount = record.getValue("commissionAmount", Double::class.java),
                    affiliateUserId = record.getValue("affiliateUserId", Long::class.java),
                    referredUserId = record.getValue("referredUserId", Long::class.java),
                    referredUserEmail = record.getValue("referredUserEmail", String::class.java)
                )
            }
    }

    override fun upsert(commission: Commission): Commission {
        val record = commission.toRecord()

        return dslContext.insertInto(COMMISSIONS)
            .set(record)
            .onConflictOnConstraint(Keys.COMMISSIONS_REFERRAL_ID_KEY)
            .doUpdate()
            .set(record)
            .returning()
            .fetchOne()
            ?.toDomain()!!
    }

    override fun updateStatus(ids: Collection<Long>, status: CommissionStatus) {
        dslContext.update(COMMISSIONS)
            .set(COMMISSIONS.STATUS, status.value)
            .set(COMMISSIONS.UPDATED_AT, clock.instant())
            .where(COMMISSIONS.ID.`in`(ids))
            .execute()
    }

    override fun sumByUserIdAndStatus(userId: Long, status: CommissionStatus): Double {
        return dslContext.select(DSL.sum(COMMISSIONS.AMOUNT))
            .from(COMMISSIONS)
            .join(REFERRALS)
            .on(COMMISSIONS.REFERRAL_ID.eq(REFERRALS.ID))
            .join(AFFILIATES)
            .on(AFFILIATES.ID.eq(REFERRALS.AFFILIATE_ID))
            .where(COMMISSIONS.STATUS.eq(status.value).and(AFFILIATES.USER_ID.eq(userId)))
            .fetchOne(0, Double::class.java) ?: 0.0
    }

    private fun listAllColumns(): List<TableField<CommissionsRecord, out Any>> {
        return listOf(
            COMMISSIONS.ID,
            COMMISSIONS.STATUS,
            COMMISSIONS.REFERRAL_ID,
            COMMISSIONS.AMOUNT,
            COMMISSIONS.CREATED_AT,
            COMMISSIONS.UPDATED_AT
        )
    }
}
