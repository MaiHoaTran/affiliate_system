package com.assignment.affiliate.usecases.subscription

import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.domain.user.UserRepository
import com.assignment.affiliate.usecases.commission.CreateCommission
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HandleSubscriptionWebhookEventTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val saveSubscription = mockk<SaveSubscription>(relaxed = true)
    private val createCommissionIfMissing = mockk<CreateCommission>(relaxed = true)
    private val handleSubscriptionWebhookEvent = HandleSubscriptionWebhookEvent(
        userRepository,
        saveSubscription,
        createCommissionIfMissing
    )

    @Test
    fun `should handle valid payload and create subscription and commission`() {
        val payload = """
        {
            "event_type": "BILLING.SUBSCRIPTION.CREATED",
            "resource": {
                "payer": {
                    "payer_info": {
                        "email": "test@example.com"
                    }
                }
            }
        }
        """.trimIndent()

        val userId = 1L
        val user = User(id = userId, email = "test@example.com", password = "123456789")
        every { userRepository.findByEmail("test@example.com") } returns user

        handleSubscriptionWebhookEvent(payload)

        verify { userRepository.findByEmail("test@example.com") }
        verify { saveSubscription(userId, SubscriptionStatus.PENDING) }
        verify { createCommissionIfMissing(userId) }
    }

    @Test
    fun `should handle valid payload with different subscription status`() {
        val payload = """
        {
            "event_type": "BILLING.SUBSCRIPTION.ACTIVATED",
            "resource": {
                "payer": {
                    "payer_info": {
                        "email": "test@example.com"
                    }
                }
            }
        }
        """.trimIndent()

        val userId = 1L
        val user = User(id = userId, email = "test@example.com", password = "12456789")
        every { userRepository.findByEmail("test@example.com") } returns user

        handleSubscriptionWebhookEvent(payload)

        verify { userRepository.findByEmail("test@example.com") }
        verify { saveSubscription(1, SubscriptionStatus.ACTIVE) }
        verify { createCommissionIfMissing(1) }
    }

    @Test
    fun `should handle invalid payload with missing resource field`() {
        val payload = """
        {
            "event_type": "BILLING.SUBSCRIPTION.CREATED"
        }
        """.trimIndent()

        assertThrows<InvalidPaypalPayloadEventException> {
            handleSubscriptionWebhookEvent(payload)
        }
    }

    @Test
    fun `should handle payload with unknown event type`() {
        val payload = """
        {
            "event_type": "UNKNOWN.EVENT.TYPE",
            "resource": {
                "payer": {
                    "payer_info": {
                        "email": "test@example.com"
                    }
                }
            }
        }
        """.trimIndent()

        val user = User(id = 1, email = "test@example.com", password = "123789")
        every { userRepository.findByEmail("test@example.com") } returns user

        handleSubscriptionWebhookEvent(payload)

        verify(exactly = 0) { saveSubscription(any(), any()) }
        verify(exactly = 0) { createCommissionIfMissing(1) }
    }
}
