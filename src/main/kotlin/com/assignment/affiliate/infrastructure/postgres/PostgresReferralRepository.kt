package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.referral.Referral
import com.assignment.affiliate.domain.referral.ReferralRepository
import com.assignment.affiliate.usecases.referral.ReferralDTO
import com.assignment.affiliate.usecases.referral.ReferralStatus
import com.assignment.affiliate.usecases.referral.toReferralStatus
import org.jooq.DSLContext
import org.jooq.generated.Tables.AFFILIATES
import org.jooq.generated.Tables.COMMISSIONS
import org.jooq.generated.Tables.REFERRALS
import org.jooq.generated.Tables.USERS
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class PostgresReferralRepository(
    private val dslContext: DSLContext
) : ReferralRepository {
    override fun findByReferredUserId(referredUserId: Long): Referral? {
        return dslContext.selectFrom(REFERRALS)
            .where(REFERRALS.REFERRED_USER_ID.eq(referredUserId))
            .fetchOneInto(Referral::class.java)
    }

    override fun findAllByAffiliateId(affiliateId: Long): List<Referral> {
        return dslContext.selectFrom(REFERRALS)
            .where(REFERRALS.AFFILIATE_ID.eq(affiliateId))
            .fetchInto(Referral::class.java)
    }

    override fun getAllReferralDTOsByUserId(userId: Long): List<ReferralDTO> {
        return dslContext.select(
            REFERRALS.ID.`as`("referralId"),
            USERS.ID.`as`("referredUserId"),
            USERS.EMAIL.`as`("referredUserEmail"),
            DSL.case_()
                .`when`(COMMISSIONS.ID.isNotNull, ReferralStatus.CONVERTED.value)
                .otherwise(ReferralStatus.PENDING.value).`as`("referralStatus"),
            COMMISSIONS.AMOUNT.`as`("commissionAmount")
        )
            .from(REFERRALS)
            .join(AFFILIATES).on(AFFILIATES.ID.eq(REFERRALS.AFFILIATE_ID))
            .join(USERS).on(REFERRALS.REFERRED_USER_ID.eq(USERS.ID))
            .leftJoin(COMMISSIONS).on(COMMISSIONS.REFERRAL_ID.eq(REFERRALS.ID))
            .where(AFFILIATES.USER_ID.eq(userId))
            .fetch { record ->
                ReferralDTO(
                    referralId = record.get("referralId", Long::class.java),
                    referralStatus = record.get("referralStatus", String::class.java).toReferralStatus(),
                    referredUserId = record.get("referredUserId", Long::class.java),
                    referredUserEmail = record.get("referredUserEmail", String::class.java),
                    commissionAmount = record.get("commissionAmount")?.toString()?.toDouble()
                )
            }
    }

    override fun save(referral: Referral): Referral {
        return dslContext.insertInto(REFERRALS)
            .set(dslContext.newRecord(REFERRALS, referral))
            .returning()
            .fetchOneInto(Referral::class.java)!!
    }
}
