package com.assignment.affiliate.usecases.user

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.Base64
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class GenerateJwtTest {
    private lateinit var generateJwt: GenerateJwt

    @MockK
    private lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        val fixedInstant = Instant.parse("2023-01-01T00:00:00Z")
        every { clock.instant() } returns fixedInstant
        every { clock.zone } returns ZoneId.of("UTC")

        val jwtSecret = "mysecretkey12345678901234567890123" // Should be at least 256 bits for HS256
        generateJwt = GenerateJwt(clock, jwtSecret)
    }

    @Test
    fun `should generate JWT with correct claims and signature`() {
        val authentication = mockk<Authentication>()
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        every { authentication.name } returns "user"
        every { authentication.authorities } returns authorities

        val jwt = generateJwt(authentication)

        assertTrue(jwt.isNotBlank())
        assertTrue(jwt.split(".").size == 3)

        val decodePayload = Base64.getDecoder().decode(jwt.split(".")[1])
        val payload = JSONObject(String(decodePayload, Charsets.UTF_8))

        assertEquals("user", payload.getString("username"))
        assertEquals("ROLE_USER", payload.getString("authorities"))
    }
}
