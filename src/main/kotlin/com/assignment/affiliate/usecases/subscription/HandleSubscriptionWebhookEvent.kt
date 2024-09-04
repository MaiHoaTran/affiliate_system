package com.assignment.affiliate.usecases.subscription

import com.assignment.affiliate.domain.exception.UserNotFoundException
import com.assignment.affiliate.domain.subscription.SubscriptionStatus
import com.assignment.affiliate.domain.user.UserRepository
import com.assignment.affiliate.usecases.commission.CreateCommissionForReferredUserId
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Use case to handle subscription webhook event:
 * - Create/update subscription based on the subscription webhook event type
 * - Create commission for given user's email if there is no commission exists in DB
 */
@Service
class HandleSubscriptionWebhookEvent(
    private val userRepository: UserRepository,
    private val saveSubscription: SaveSubscription,
    private val createCommissionForReferredUserId: CreateCommissionForReferredUserId
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    operator fun invoke(payload: String) {
        logger.debug("Handle subscription webhook event with payload: {}", payload)

        val payloadJsonObject = Json.parseToJsonElement(payload).jsonObject
        val eventType = payloadJsonObject["event_type"]?.jsonPrimitive?.content
        val resource = payloadJsonObject["resource"]?.jsonObject
            ?: throw InvalidPaypalPayloadEventException("resource")
        val userId = getUserId(resource)
        val subscriptionStatus = getSubscriptionStatus(eventType)

        if (subscriptionStatus == null) {
            logger.warn("Cannot found subscription status from webhook payload: {}", payload)
            return
        }

        saveSubscription(userId, subscriptionStatus)
        createCommissionForReferredUserId(userId)

        logger.debug("Finishing handle subscription webhook event")
    }

    private fun getUserId(resource: JsonObject): Long {
        val email = getEmail(resource) ?: throw InvalidPaypalPayloadEventException("email")
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User with email $email not found")
        return user.id!!
    }

    private fun getEmail(resource: JsonObject): String? {
        val email = resource["payer"]?.jsonObject?.get("payer_info")?.jsonObject?.get("email")?.jsonPrimitive?.content
        if (email != null) {
            return email
        }
        return resource["subscriber"]?.jsonObject?.get("email_address")?.jsonPrimitive?.content
    }

    private fun getSubscriptionStatus(eventType: String?): SubscriptionStatus? {
        return when (eventType) {
            "BILLING.SUBSCRIPTION.CREATED" -> SubscriptionStatus.PENDING
            "BILLING.SUBSCRIPTION.ACTIVATED" -> SubscriptionStatus.ACTIVE
            "BILLING.SUBSCRIPTION.SUSPENDED" -> SubscriptionStatus.SUSPENDED
            "BILLING.SUBSCRIPTION.EXPIRED" -> SubscriptionStatus.EXPIRED
            "BILLING.SUBSCRIPTION.CANCELLED" -> SubscriptionStatus.CANCELED
            "BILLING.SUBSCRIPTION.RE-ACTIVATED" -> SubscriptionStatus.ACTIVE
            else -> null
        }
    }
}
