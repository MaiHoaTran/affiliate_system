package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.TestConfig
import com.assignment.affiliate.api.http.AuthenticatedEndpointTest
import com.assignment.affiliate.usecases.user.GenerateJwt
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import java.util.Base64

class UserEndpointTest : AuthenticatedEndpointTest() {
    @MockBean
    private lateinit var generateJwt: GenerateJwt

    @Test
    fun `generateToken - runtime exception - response with error 500`() {
        whenever(generateJwt(any())).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildGenerateJWTRequest())
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

    @ParameterizedTest
    @CsvSource("true", "false")
    fun `generateToken - generate JWT successfully, response 200`(isAdmin: Boolean) {
        whenever(generateJwt.invoke(any()))
            .thenReturn("token")

        mockMvc.perform(buildGenerateJWTRequest())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().string("token"))
    }

    private fun buildGenerateJWTRequest(isAdmin: Boolean = true): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post("/api/v1/users/jwt")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", createBasicToken(isAdmin))
    }

    private fun createBasicToken(isAdmin: Boolean): String {
        val credentials = if (isAdmin) {
            "${TestConfig.ADMIN_EMAIL}:${TestConfig.ADMIN_PASSWORD}"
        } else {
            "${TestConfig.USER_EMAIL}:${TestConfig.USER_PASSWORD}"
        }
        val token = Base64.getEncoder().encodeToString(credentials.toByteArray(Charsets.UTF_8))

        return "Basic $token"
    }
}
