package com.assignment.affiliate.api.http

import com.assignment.affiliate.TestConfig
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import java.util.UUID

@EndpointTest
class AuthenticatedEndpointTest {
    @Value("\${spring.security.jwt.secret}")
    private lateinit var jwtSecret: String

    @Autowired
    internal lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockkStatic(UUID::class)

        val mockUUID = mockk<UUID>()
        every { mockUUID.toString() } returns ERROR_ID
        every { UUID.randomUUID() } returns mockUUID
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(UUID::class)
    }

    internal fun authenticateRequest(
        request: MockHttpServletRequestBuilder,
        isAdmin: Boolean = true
    ): MockHttpServletRequestBuilder {
        val token = createBearerToken(isAdmin)
        return request.header(HttpHeaders.AUTHORIZATION, token)
    }

    private fun createBearerToken(isAdmin: Boolean): String {
        val token = Jwts.builder()
            .claim("username", if (isAdmin) TestConfig.ADMIN_EMAIL else TestConfig.USER_EMAIL)
            .claim("authorities", if (isAdmin) "ROLE_ADMIN" else "ROLE_USER")
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .compact()
        return "Bearer $token"
    }

    companion object {
        const val ERROR_ID = "00000000000000000000000000000000"
    }
}
