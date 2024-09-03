package com.assignment.affiliate.api.http.v1.admin

import com.assignment.affiliate.api.http.AuthenticatedEndpointTest
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.usecases.commission.CommissionDTO
import com.assignment.affiliate.usecases.commission.GetAllCommissionsByAffiliateId
import com.assignment.affiliate.usecases.commission.UpdateCommissionStatusByCommissionId
import com.assignment.affiliate.usecases.commission.UpdateCommissionStatusesByAffiliateId
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import kotlin.test.Test

class AdminCommissionEndpointTest : AuthenticatedEndpointTest() {
    @MockBean
    private lateinit var getAllCommissionsByAffiliateId: GetAllCommissionsByAffiliateId

    @MockBean
    private lateinit var updateCommissionStatusesByAffiliateId: UpdateCommissionStatusesByAffiliateId

    @MockBean
    private lateinit var updateCommissionStatusByCommissionId: UpdateCommissionStatusByCommissionId

    @Test
    fun `updateStatusByCommissionId - unauthenticated - response with error 401`() {
        mockMvc.perform(buildUpdateStatusByCommissionIdRequest(isAuthenticated = false))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `updateStatusByCommissionId - runtime exception - response with error 500`() {
        whenever(updateCommissionStatusByCommissionId(any(), any())).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildUpdateStatusByCommissionIdRequest())
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
    fun `updateStatusByCommissionId - unknown status - response with error 400`() {
        mockMvc.perform(buildUpdateStatusByCommissionIdRequest(requestBody = """{"status":"unknown"}"""))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                content().json(
                    """
                    {
                        "errors": [
                            {
                                "id": "$ERROR_ID",
                                "code": "BAD_REQUEST",
                                "title": "Invalid commission status",
                                "detail": null
                            }
                        ]
                    }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `updateStatusByCommissionId - valid response - update successfully, response 204`() {
        doNothing().`when`(updateCommissionStatusByCommissionId).invoke(any(), any())

        mockMvc.perform(
            buildUpdateStatusByCommissionIdRequest(
                commissionId = 2L,
                requestBody = """{"status":"approved"}"""
            )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun `updateStatusByAffiliateId - unauthenticated - response with error 401`() {
        mockMvc.perform(buildGetTotalCommissionAmountRequest(isAuthenticated = false))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `updateStatusByAffiliateId - runtime exception - response with error 500`() {
        whenever(updateCommissionStatusesByAffiliateId(any(), any())).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildGetTotalCommissionAmountRequest())
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
    fun `updateStatusByAffiliateId - unknown status - response with error 400`() {
        mockMvc.perform(buildGetTotalCommissionAmountRequest(requestBody = """{"status":"unknown"}"""))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                content().json(
                    """
                    {
                        "errors": [
                            {
                                "id": "$ERROR_ID",
                                "code": "BAD_REQUEST",
                                "title": "Invalid commission status",
                                "detail": null
                            }
                        ]
                    }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `updateStatusByAffiliateId - valid status - update successfully, response 204`() {
        doNothing().`when`(updateCommissionStatusesByAffiliateId).invoke(any(), any())

        mockMvc.perform(
            buildUpdateStatusByCommissionIdRequest(
                commissionId = 2L,
                requestBody = """{"status":"approved"}"""
            )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun `listAllCommissionByAffiliateId - authenticated - response list of commissions`() {
        val affiliateId = 100L
        whenever(getAllCommissionsByAffiliateId.invoke(affiliateId)).thenReturn(
            listOf(
                CommissionDTO(
                    commissionId = 1L,
                    commissionAmount = 19.0,
                    commissionStatus = CommissionStatus.APPROVED,
                    affiliateUserId = affiliateId,
                    referredUserId = 1L,
                    referredUserEmail = "user1@test.com"
                ),
                CommissionDTO(
                    commissionId = 2L,
                    commissionAmount = 19.1,
                    commissionStatus = CommissionStatus.PENDING,
                    affiliateUserId = affiliateId,
                    referredUserId = 2L,
                    referredUserEmail = "user2@test.com"
                )
            )
        )

        val request = authenticateRequest(
            MockMvcRequestBuilders
                .get("/api/v1/admin/affiliate/$affiliateId/commissions")
                .contentType(MediaType.APPLICATION_JSON)
        )

        mockMvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                content().json(
                    """
                    [
                        {
                            "commission_id": 1,
                            "commission_status": "approved",
                            "commission_amount": 19.0,
                            "referred_user_id": 1,
                            "referred_user_email": "user1@test.com"
                        },
                        {
                            "commission_id": 2,
                            "commission_status": "pending",
                            "commission_amount": 19.1,
                            "referred_user_id": 2,
                            "referred_user_email": "user2@test.com"
                        }
                    ]
                    """.trimIndent()
                )
            )
    }

    private fun buildUpdateStatusByCommissionIdRequest(
        isAuthenticated: Boolean = true,
        commissionId: Long = 1L,
        requestBody: String? = """{"status": "rejected"}"""
    ): MockHttpServletRequestBuilder {
        var request = MockMvcRequestBuilders
            .patch("/api/v1/admin/commission/$commissionId")
            .contentType(MediaType.APPLICATION_JSON)

        requestBody?.let {
            request = request.content(it)
        }

        return if (isAuthenticated) authenticateRequest(request) else request
    }

    private fun buildGetTotalCommissionAmountRequest(
        isAuthenticated: Boolean = true,
        affiliateId: Long = 1L,
        requestBody: String? = """{"status": "rejected"}"""
    ): MockHttpServletRequestBuilder {
        var request = MockMvcRequestBuilders
            .patch("/api/v1/admin/affiliate/$affiliateId/commissions")
            .contentType(MediaType.APPLICATION_JSON)

        requestBody?.let {
            request = request.content(it)
        }

        return if (isAuthenticated) authenticateRequest(request) else request
    }
}
