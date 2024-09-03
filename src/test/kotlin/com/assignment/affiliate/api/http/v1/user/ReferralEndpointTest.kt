package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.api.http.AuthenticatedEndpointTest
import com.assignment.affiliate.usecases.referral.GetAllReferralsByUserId
import com.assignment.affiliate.usecases.referral.ReferralDTO
import com.assignment.affiliate.usecases.referral.ReferralStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

class ReferralEndpointTest : AuthenticatedEndpointTest() {
    @MockBean
    private lateinit var getAllReferralsByUserId: GetAllReferralsByUserId

    @Test
    fun `getAffiliates - unauthenticated - response with error 401`() {
        mockMvc.perform(buildGetReferralsRequest(isAuthenticated = false))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `getAffiliates - runtime exception - response with error 500`() {
        whenever(getAllReferralsByUserId(any())).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildGetReferralsRequest())
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

    @CsvSource("true", "false")
    @ParameterizedTest
    fun `getAffiliates - authenticated - response list of referrals`(isAdmin: Boolean) {
        val userId = 111L

        whenever(getAllReferralsByUserId.invoke(userId)).thenReturn(
            listOf(
                ReferralDTO(
                    referralId = 1L,
                    referralStatus = ReferralStatus.CONVERTED,
                    referredUserId = 1L,
                    referredUserEmail = "user1@test.com",
                    commissionAmount = 10.9
                ),
                ReferralDTO(
                    referralId = 1L,
                    referralStatus = ReferralStatus.PENDING,
                    referredUserId = 2L,
                    referredUserEmail = "user2@test.com",
                    commissionAmount = 19.0
                )
            )
        )

        mockMvc.perform(buildGetReferralsRequest(userId = userId, isAdmin = isAdmin))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                content().json(
                    """
                    [
                        {
                            "referral_id": 1,
                            "referral_status": "converted",
                            "referred_user_id": 1,
                            "referred_user_email": "user1@test.com",
                            "commission_amount": 10.9
                        },
                        {
                            "referral_id": 1,
                            "referral_status": "pending",
                            "referred_user_id": 2,
                            "referred_user_email": "user2@test.com",
                            "commission_amount": 19.0
                        }
                    ]
                    """.trimIndent()
                )
            )
    }

    private fun buildGetReferralsRequest(
        isAuthenticated: Boolean = true,
        isAdmin: Boolean = false,
        userId: Long = 100L
    ): RequestBuilder {
        val request = MockMvcRequestBuilders
            .get("/api/v1/user/$userId/referrals")
            .contentType(MediaType.APPLICATION_JSON)

        return if (isAuthenticated) authenticateRequest(request, isAdmin) else request
    }
}
