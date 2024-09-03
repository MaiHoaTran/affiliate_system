package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.affiliate.AffiliateRepository
import com.assignment.affiliate.domain.exception.DuplicateAffiliateCodeException
import com.assignment.affiliate.domain.exception.DuplicateAffiliateUserException
import com.assignment.affiliate.usecases.affiliate.AffiliateDTO
import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.generated.Keys
import org.jooq.generated.Tables.USERS
import org.jooq.generated.tables.Affiliates.AFFILIATES
import org.jooq.generated.tables.records.AffiliatesRecord
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository

@Repository
class PostgresAffiliateRepository(
    private val dslContext: DSLContext
) : AffiliateRepository {
    override fun findAll(): List<AffiliateDTO> {
        return dslContext.select(
            AFFILIATES.ID.`as`("affiliateId"),
            AFFILIATES.AFFILIATE_CODE.`as`("affiliateCode"),
            USERS.ID.`as`("affiliateUserId"),
            USERS.EMAIL.`as`("affiliateUserEmail")
        )
            .from(AFFILIATES)
            .join(USERS)
            .on(AFFILIATES.USER_ID.eq(USERS.ID))
            .fetchInto(AffiliateDTO::class.java)
    }

    override fun findByUserId(userId: Long): Affiliate? {
        return dslContext.select(listAllColumns())
            .from(AFFILIATES)
            .where(AFFILIATES.USER_ID.eq(userId))
            .fetchOneInto(Affiliate::class.java)
    }

    override fun findByAffiliateCode(affiliateCode: String): Affiliate? {
        return dslContext.select(listAllColumns())
            .from(AFFILIATES)
            .where(AFFILIATES.AFFILIATE_CODE.eq(affiliateCode))
            .fetchOneInto(Affiliate::class.java)
    }

    override fun save(affiliate: Affiliate): Affiliate = runCatching {
        dslContext.insertInto(AFFILIATES)
            .set(dslContext.newRecord(AFFILIATES, affiliate))
            .returning()
            .fetchOneInto(Affiliate::class.java)!!
    }.getOrElse { exception ->
        if (exception is DataIntegrityViolationException) {
            val cause = exception.rootCause?.message
            if (cause?.contains(Keys.AFFILIATES_AFFILIATE_CODE_KEY.name) == true) {
                throw DuplicateAffiliateCodeException()
            }
            if (cause?.contains(Keys.AFFILIATES_USER_ID_KEY.name) == true) {
                throw DuplicateAffiliateUserException()
            }
            throw exception
        } else {
            throw exception
        }
    }

    private fun listAllColumns(): List<TableField<AffiliatesRecord, out Any>> {
        return listOf(
            AFFILIATES.ID,
            AFFILIATES.USER_ID,
            AFFILIATES.AFFILIATE_CODE,
            AFFILIATES.CREATED_AT
        )
    }
}
