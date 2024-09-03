package com.assignment.affiliate.infrastructure.postgres

import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.domain.user.UserRepository
import org.jooq.DSLContext
import org.jooq.generated.Tables.USERS
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@PostgresTest
class PostgresUserRepositoryTest {
    @Autowired
    private lateinit var dslContext: DSLContext

    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository = PostgresUserRepository(dslContext)
        dslContext.deleteFrom(USERS)
    }

    @Test
    fun `findById - user does not exist - return null`() {
        assertNull(this.userRepository.findById(1L))
    }

    @Test
    fun `findById - user does exist - return found one`() {
        val userId = createUser(email = "email@test.com").id!!

        val user = this.userRepository.findById(userId)

        assertNotNull(user)
        assertEquals(userId, user.id!!)
    }

    @Test
    fun `findByEmail - user does not exist - return null`() {
        assertNull(this.userRepository.findByEmail("email@test.com"))
    }

    @Test
    fun `findByEmail - user does exist - return found one`() {
        val email = "email@test.com"
        createUser(email = email)

        val user = this.userRepository.findByEmail("email@test.com")

        assertNotNull(user)
        assertEquals(email, user.email)
    }

    @Test
    fun `upsert - user does not exist - create new one`() {
        val email = "test@test.com"
        assertNull(userRepository.findByEmail(email))

        userRepository.upsert(User(email = email, password = "123456789"))

        val createdUser = userRepository.findByEmail(email)
        assertNotNull(createdUser)
    }

    @Test
    fun `upsert - user does exist - update existing user`() {
        val email = "test@test.com"
        val now = Instant.now()
        createUser(email = email, createdAt = now, updateAt = now)

        val updatedAt = now.plusMillis(2000)
        userRepository.upsert(User(email = email, password = "123456789", createdAt = Instant.now(), updatedAt = updatedAt))

        val updatedUser = userRepository.findByEmail(email)
        assertEquals(updatedAt, updatedUser!!.updatedAt)
    }

    private fun createUser(email: String, createdAt: Instant? = null, updateAt: Instant? = null): User {
        return dslContext.insertInto(USERS)
            .set(
                dslContext.newRecord(
                    USERS,
                    User(email = email, password = "123456789", createdAt = createdAt, updatedAt = updateAt)
                )
            )
            .returning()
            .fetchOneInto(User::class.java)!!
    }
}
