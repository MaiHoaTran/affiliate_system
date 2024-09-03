package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.api.http.AuthenticatedEndpointTest.Companion.ERROR_ID
import com.assignment.affiliate.api.http.EndpointTest
import com.assignment.affiliate.usecases.signup.Signup
import com.assignment.affiliate.usecases.signup.UserDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import java.util.UUID

@EndpointTest
class SignupEndpointTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var signup: Signup

    @BeforeEach
    fun setUp() {
        mockkStatic(UUID::class)

        val mockUUID = mockk<UUID>()
        every { mockUUID.toString() } returns ERROR_ID
        every { UUID.randomUUID() } returns mockUUID
    }

    @Test
    fun `postSignup - runtime exception - response with error 500`() {
        whenever(signup(any(), any(), any())).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildSignupRequest())
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andExpect(
                content().json(
                    """
                    {
                        "errors": [
                            {
                                "id": "$ERROR_ID",
                                "code": "SERVER_ERROR",
                                "title": "Something went wrong: Unknown exception",
                                "detail": null
                            }
                        ]
                    }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `postSignup - missing email - response with error 400`() {
        mockMvc.perform(buildSignupRequest(requestBody = """{"password":"password"}"""))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `signup - valid request body - signup successfully, response 200`() {
        whenever(signup.invoke(any(), any(), any()))
            .thenReturn(UserDTO(email = "test@test.com", affiliateCode = "code"))

        mockMvc.perform(buildSignupRequest())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                        "email": "test@test.com",
                        "affiliate_code": "code"
                    }
                    """.trimIndent()
                )
            )
    }

    private fun buildSignupRequest(
        requestBody: String? = """{"email": "test@test.com","password":"123456789","ref_code":"12345"}"""
    ): MockHttpServletRequestBuilder {
        var request = MockMvcRequestBuilders
            .post("/api/v1/signup")
            .contentType(MediaType.APPLICATION_JSON)

        requestBody?.let {
            request = request.content(it)
        }

        return request
    }
}
