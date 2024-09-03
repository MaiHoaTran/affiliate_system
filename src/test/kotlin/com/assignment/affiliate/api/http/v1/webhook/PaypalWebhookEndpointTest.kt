package com.assignment.affiliate.api.http.v1.webhook

import com.assignment.affiliate.api.http.AuthenticatedEndpointTest.Companion.ERROR_ID
import com.assignment.affiliate.api.http.EndpointTest
import com.assignment.affiliate.usecases.subscription.HandleSubscriptionWebhookEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
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
class PaypalWebhookEndpointTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var handleSubscriptionWebhookEvent: HandleSubscriptionWebhookEvent

    @BeforeEach
    fun setUp() {
        mockkStatic(UUID::class)

        val mockUUID = mockk<UUID>()
        every { mockUUID.toString() } returns ERROR_ID
        every { UUID.randomUUID() } returns mockUUID
    }

    @Test
    fun `postSignup - runtime exception - response with error 500`() {
        whenever(handleSubscriptionWebhookEvent(any())).thenThrow(RuntimeException("Unknown exception"))

        mockMvc.perform(buildHandlePayPalWebhookEventRequest())
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
    fun `postSignup - missing payload - response with error 400`() {
        mockMvc.perform(buildHandlePayPalWebhookEventRequest(requestBody = ""))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `signup - valid request body - signup successfully, response 200`() {
        doNothing().`when`(handleSubscriptionWebhookEvent).invoke(any())

        mockMvc.perform(buildHandlePayPalWebhookEventRequest())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    private fun buildHandlePayPalWebhookEventRequest(
        requestBody: String? = """{"event_type": "BILLING.SUBSCRIPTION.CREATED","resources":{"payer":{"payer_info":{"email":"user@test.com"}}}}"""
    ): MockHttpServletRequestBuilder {
        var request = MockMvcRequestBuilders
            .post("/api/v1/webhook/paypal")
            .contentType(MediaType.TEXT_PLAIN_VALUE)

        requestBody?.let {
            request = request.content(it)
        }

        return request
    }
}
