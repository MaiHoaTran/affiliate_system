package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.domain.user.UserRepository
import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.generated.Keys
import org.jooq.generated.tables.Users.USERS
import org.jooq.generated.tables.records.UsersRecord
import org.springframework.stereotype.Repository

@Repository
class PostgresUserRepository(private val dslContext: DSLContext) : UserRepository {
    override fun findById(id: Long): User? {
        return dslContext.select(listAllColumns())
            .from(USERS)
            .where(USERS.ID.eq(id))
            .fetchOneInto(User::class.java)
    }

    override fun findByEmail(email: String): User? {
        return dslContext.select(listAllColumns())
            .from(USERS)
            .where(USERS.EMAIL.eq(email))
            .fetchOneInto(User::class.java)
    }

    override fun upsert(user: User): User {
        val record = dslContext.newRecord(USERS, user)

        return dslContext.insertInto(USERS)
            .set(record)
            .onConflictOnConstraint(Keys.USERS_EMAIL_KEY)
            .doUpdate()
            .set(record)
            .returning()
            .fetchOneInto(User::class.java)!!
    }

    private fun listAllColumns(): List<TableField<UsersRecord, out Any>> {
        return listOf(
            USERS.ID,
            USERS.EMAIL,
            USERS.PASSWORD,
            USERS.IS_ADMIN,
            USERS.CREATED_AT,
            USERS.UPDATED_AT
        )
    }
}
