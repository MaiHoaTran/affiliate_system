package com.assignment.affiliate.api.http.v1.admin

import com.assignment.affiliate.api.http.AuthenticatedEndpointTest
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.usecases.user.CreateUser
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

class AdminUserEndpointEndpointTest : AuthenticatedEndpointTest() {
    @MockBean
    private lateinit var createUser: CreateUser

    @Test
    fun `createAdminUser - unauthenticated - response with error 401`() {
        mockMvc.perform(buildCreateAdminUserRequest(isAuthenticated = false))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `createAdminUser - authenticate user is not admin - response with error 403`() {
        mockMvc.perform(buildCreateAdminUserRequest(isAuthenticated = true, isAdmin = false))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `createAdminUser - runtime exception - response with error 500`() {
        whenever(createUser(any(), any(), any())).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildCreateAdminUserRequest())
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
    fun `createAdminUser - missing email - response with error 400`() {
        mockMvc.perform(buildCreateAdminUserRequest(requestBody = """{"password":"password"}"""))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `createAdminUser - valid request body - create successfully, response 204`() {
        val user = mockk<User>()
        whenever(createUser.invoke(any(), any(), any())).thenReturn(user)

        mockMvc.perform(buildCreateAdminUserRequest())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    private fun buildCreateAdminUserRequest(
        isAuthenticated: Boolean = true,
        isAdmin: Boolean = true,
        requestBody: String? = """{"email": "test@test.com","password":"123456789"}"""
    ): MockHttpServletRequestBuilder {
        var request = MockMvcRequestBuilders
            .post("/api/v1/admin/users")
            .contentType(MediaType.APPLICATION_JSON)

        requestBody?.let {
            request = request.content(it)
        }

        return if (isAuthenticated) authenticateRequest(request, isAdmin) else request
    }
}
