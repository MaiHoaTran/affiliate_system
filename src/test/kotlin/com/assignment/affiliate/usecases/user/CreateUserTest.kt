package com.assignment.affiliate.usecases.user

import com.assignment.affiliate.domain.exception.DuplicateUserException
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.domain.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Clock
import java.time.Instant

class CreateUserTest {
    private val clock = mockk<Clock>()
    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val createUser = CreateUser(clock, userRepository, passwordEncoder)

    @Test
    fun `should create user when email does not exist`() {
        val email = "test@example.com"
        val password = "password123"
        val now = Instant.now()
        val newUser = User(
            email = email,
            createdAt = now,
            password = password
        )

        every { clock.instant() } returns now
        every { userRepository.findByEmail(email) } returns null
        every { userRepository.upsert(newUser) } returns newUser
        every { passwordEncoder.encode(password) } returns password

        val result = createUser(email, password)

        verify { userRepository.findByEmail(email) }
        verify { userRepository.upsert(newUser) }
        verify { passwordEncoder.encode(password) }
        assertEquals(newUser, result)
    }

    @Test
    fun `should throw DuplicateUserException when email already exists`() {
        val email = "test@example.com"
        val password = "password123"

        every { userRepository.findByEmail(email) } returns User(
            email = email,
            createdAt = Instant.now(),
            password = password
        )

        assertThrows<DuplicateUserException> {
            createUser(email, password)
        }
        verify { userRepository.findByEmail(email) }
        verify(exactly = 0) { userRepository.upsert(any()) }
    }
}
