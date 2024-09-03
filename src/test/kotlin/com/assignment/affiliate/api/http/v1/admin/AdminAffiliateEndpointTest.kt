package com.assignment.affiliate.api.http.v1.admin

import com.assignment.affiliate.api.http.AuthenticatedEndpointTest
import com.assignment.affiliate.usecases.affiliate.AffiliateDTO
import com.assignment.affiliate.usecases.affiliate.GetAllAffiliates
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

class AdminAffiliateEndpointTest : AuthenticatedEndpointTest() {
    @MockBean
    private lateinit var getAllAffiliates: GetAllAffiliates

    @Test
    fun `getAffiliates - unauthenticated - response with error 401`() {
        mockMvc.perform(buildGetAffiliatesRequest(isAuthenticated = false))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `getAffiliates - runtime exception - response with error 500`() {
        whenever(getAllAffiliates()).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildGetAffiliatesRequest())
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
    fun `getAffiliates - authenticated - response list of commissions`() {
        whenever(getAllAffiliates.invoke()).thenReturn(
            listOf(
                AffiliateDTO(
                    affiliateId = 1L,
                    affiliateCode = "0987654321",
                    affiliateUserId = 1L,
                    affiliateUserEmail = "user2@test.com"
                ),
                AffiliateDTO(
                    affiliateId = 2L,
                    affiliateCode = "1234567890",
                    affiliateUserId = 2L,
                    affiliateUserEmail = "user2@test.com"
                )
            )
        )

        mockMvc.perform(buildGetAffiliatesRequest())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                content().json(
                    """
                    [
                        {
                            "affiliate_id": 1,
                            "affiliate_code": "0987654321",
                            "affiliate_user_id": 1,
                            "affiliate_user_email": "user2@test.com"
                        },
                        {
                            "affiliate_id": 2,
                            "affiliate_code": "1234567890",
                            "affiliate_user_id": 2,
                            "affiliate_user_email": "user2@test.com"
                        }
                    ]
                    """.trimIndent()
                )
            )
    }

    private fun buildGetAffiliatesRequest(isAuthenticated: Boolean = true): RequestBuilder {
        val request = MockMvcRequestBuilders
            .get("/api/v1/admin/affiliates")
            .contentType(MediaType.APPLICATION_JSON)

        return if (isAuthenticated) authenticateRequest(request) else request
    }
}
