package com.assignment.affiliate.api.http.v1.webhook

import com.assignment.affiliate.usecases.subscription.HandleSubscriptionWebhookEvent
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/webhook/paypal")
class PaypalWebhookEndpoint(
    private val handleSubscriptionWebhookEvent: HandleSubscriptionWebhookEvent
) {
    @PostMapping(consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun handlePaypalWebhookEvent(@RequestBody payload: String): ResponseEntity<Unit> {
        handleSubscriptionWebhookEvent.invoke(payload)

        return ResponseEntity.noContent().build()
    }
}
