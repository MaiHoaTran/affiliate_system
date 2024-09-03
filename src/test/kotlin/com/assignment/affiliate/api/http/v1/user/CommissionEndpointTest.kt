package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.api.http.AuthenticatedEndpointTest
import com.assignment.affiliate.domain.commission.CommissionStatus
import com.assignment.affiliate.usecases.commission.CalculateCommissionAmountByUserIdAndStatus
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

class CommissionEndpointTest : AuthenticatedEndpointTest() {
    @MockBean
    private lateinit var calculateCommissionAmountByUserIdAndStatus: CalculateCommissionAmountByUserIdAndStatus

    @Test
    fun `calculateCommissionAmountByUserIdAndStatus - unauthenticated - response with error 401`() {
        mockMvc.perform(buildGetTotalCommissionAmountRequest(isAuthenticated = false))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `calculateCommissionAmountByUserIdAndStatus - runtime exception - response with error 500`() {
        whenever(calculateCommissionAmountByUserIdAndStatus(any(), any()))
            .thenThrow(RuntimeException("Unknown exception"))

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
    fun `calculateCommissionAmountByUserIdAndStatus - unknown status - response with error 400`() {
        mockMvc.perform(buildGetTotalCommissionAmountRequest(commissionStatus = "unknown"))
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

    @CsvSource("true", "false")
    @ParameterizedTest
    fun `calculateCommissionAmountByUserIdAndStatus - valid status - calculate successfully, response 200`(
        isAdmin: Boolean
    ) {
        val userId = 100L
        val status = CommissionStatus.APPROVED
        whenever(calculateCommissionAmountByUserIdAndStatus.invoke(userId, status))
            .thenReturn(30.9)

        mockMvc.perform(
            buildGetTotalCommissionAmountRequest(
                isAdmin = isAdmin,
                userId = userId,
                commissionStatus = status.value
            )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                        "total_commission_amount": 30.9
                    }
                    """.trimIndent()
                )
            )
    }

    private fun buildGetTotalCommissionAmountRequest(
        isAuthenticated: Boolean = true,
        isAdmin: Boolean = false,
        userId: Long = 1L,
        commissionStatus: String? = CommissionStatus.PENDING.value
    ): MockHttpServletRequestBuilder {
        val request = MockMvcRequestBuilders
            .get("/api/v1/user/$userId/commissions/total?status=$commissionStatus")
            .contentType(MediaType.APPLICATION_JSON)

        return if (isAuthenticated) authenticateRequest(request, isAdmin) else request
    }
}
