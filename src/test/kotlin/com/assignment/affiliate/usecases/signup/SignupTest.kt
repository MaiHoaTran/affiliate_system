package com.assignment.affiliate.usecases.signup

import com.assignment.affiliate.domain.affiliate.Affiliate
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.usecases.affiliate.CreateAffiliate
import com.assignment.affiliate.usecases.referral.CreateReferral
import com.assignment.affiliate.usecases.user.CreateUser
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SignupTest {
    private val createUser = mockk<CreateUser>()
    private val createAffiliate = mockk<CreateAffiliate>()
    private val createReferral = mockk<CreateReferral>()
    private val signup = Signup(createUser, createAffiliate, createReferral)

    @Test
    fun `should create user, affiliate, and referral and return UserDTO`() {
        val email = "test@example.com"
        val password = "password"
        val refCode = "REF123"
        val user = User(id = 1, password = "123456789", email = email)
        val affiliate = Affiliate(id = 1, affiliateCode = "AFF123", userId = user.id!!)

        every { createUser(email, password) } returns user
        every { createAffiliate(user.id!!) } returns affiliate
        justRun { createReferral(user.id!!, refCode) }

        val expectedUserDTO = UserDTO(
            email = email,
            affiliateCode = affiliate.affiliateCode
        )

        val result = signup(email, password, refCode)

        assertEquals(expectedUserDTO, result)
        verify { createUser(email, password) }
        verify { createAffiliate(user.id!!) }
        verify { createReferral(user.id!!, refCode) }
    }

    @Test
    fun `should create user and affiliate, and handle null referral code`() {
        val email = "test@example.com"
        val password = "password"
        val refCode: String? = null
        val user = User(id = 1, email = email, password = password)
        val affiliate = Affiliate(id = 1, affiliateCode = "AFF123", userId = user.id!!)

        every { createUser(email, password) } returns user
        every { createAffiliate(user.id!!) } returns affiliate

        val expectedUserDTO = UserDTO(
            email = email,
            affiliateCode = affiliate.affiliateCode
        )

        val result = signup(email, password, refCode)

        assertEquals(expectedUserDTO, result)
        verify { createUser(email, password) }
        verify { createAffiliate(user.id!!) }
        verify(exactly = 0) { createReferral(user.id!!, refCode) }
    }

    @Test
    fun `should throw exception if createUser fails`() {
        val email = "test@example.com"
        val password = "password"
        val refCode = "REF123"

        every { createUser(email, password) } throws RuntimeException("User creation failed")

        assertThrows<RuntimeException> {
            signup(email, password, refCode)
        }
    }
}
