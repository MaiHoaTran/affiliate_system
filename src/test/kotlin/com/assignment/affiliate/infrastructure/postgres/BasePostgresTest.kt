package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.commission.Commission
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.domain.referral.Referral
import com.assignment.affiliate.domain.subscription.Subscription
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.infrastructure.postgres.extension.toDomain
import com.assignment.affiliate.infrastructure.postgres.extension.toRecord
import org.jooq.DSLContext
import org.jooq.generated.Tables.AFFILIATES
import org.jooq.generated.Tables.COMMISSIONS
import org.jooq.generated.Tables.REFERRALS
import org.jooq.generated.Tables.SUBSCRIPTIONS
import org.jooq.generated.Tables.USERS
import org.jooq.generated.tables.records.CommissionsRecord
import org.jooq.generated.tables.records.SubscriptionsRecord
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.random.Random

@PostgresTest
class BasePostgresTest {
    @Autowired
    internal lateinit var dslContext: DSLContext
    internal val clock: Clock = Clock.fixed(Instant.parse("2024-09-02T01:00:00.00Z"), ZoneOffset.UTC)

    @BeforeEach
    fun setUp() {
        dslContext.deleteFrom(USERS)
        dslContext.deleteFrom(AFFILIATES)
        dslContext.deleteFrom(REFERRALS)
        dslContext.deleteFrom(COMMISSIONS)
    }

    internal fun createUser(
        id: Long? = null,
        email: String,
        password: String = "123456789",
        createdAt: Instant? = null,
        updateAt: Instant? = null
    ): User {
        return dslContext.insertInto(USERS)
            .set(
                dslContext.newRecord(
                    USERS,
                    User(id = id, email = email, createdAt = createdAt, password = password, updatedAt = updateAt)
                )
            )
            .returning()
            .fetchOneInto(User::class.java)!!
    }

    internal fun createAffiliate(
        id: Long? = null,
        userId: Long,
        affiliateCode: String,
        createdAt: Instant? = null
    ): Affiliate {
        return dslContext.insertInto(AFFILIATES)
            .set(
                dslContext.newRecord(
                    AFFILIATES,
                    Affiliate(id = id, userId = userId, affiliateCode = affiliateCode, createdAt = createdAt)
                )
            )
            .returning()
            .fetchOneInto(Affiliate::class.java)!!
    }

    internal fun createReferral(
        id: Long? = null,
        affiliateId: Long,
        referredUserId: Long,
        createdAt: Instant? = null
    ): Referral {
        return dslContext.insertInto(REFERRALS)
            .set(
                dslContext.newRecord(
                    REFERRALS,
                    Referral(
                        id = id,
                        affiliateId = affiliateId,
                        referredUserId = referredUserId,
                        createdAt = createdAt
                    )
                )
            )
            .returning()
            .fetchOneInto(Referral::class.java)!!
    }

    internal fun createCommission(
        id: Long? = null,
        referralId: Long,
        status: CommissionStatus = CommissionStatus.PENDING,
        createdAt: Instant? = clock.instant(),
        updatedAt: Instant? = clock.instant()
    ): Commission {
        return dslContext.insertInto(COMMISSIONS)
            .set(
                Commission(
                    id = id,
                    referralId = referralId,
                    status = status,
                    amount = Commission.DEFAULT_COMMISSION_AMOUNT,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                ).toRecord()
            )
            .returning()
            .fetchOneInto(CommissionsRecord::class.java)!!
            .toDomain()
    }

    internal fun createSubscription(
        id: Long? = null,
        userId: Long,
        status: SubscriptionStatus = SubscriptionStatus.PENDING,
        createdAt: Instant? = clock.instant(),
        updatedAt: Instant? = clock.instant()
    ): Subscription {
        return dslContext.insertInto(SUBSCRIPTIONS)
            .set(
                Subscription(
                    id = id,
                    userId = userId,
                    status = status,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                ).toRecord()
            )
            .returning()
            .fetchOneInto(SubscriptionsRecord::class.java)!!
            .toDomain()
    }

    internal fun generateAffiliateCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..10)
            .map { chars.random(Random) }
            .joinToString("")
    }
}
