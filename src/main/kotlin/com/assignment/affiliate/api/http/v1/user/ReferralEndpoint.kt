package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.usecases.referral.GetAllReferralsByUserId
import com.assignment.affiliate.usecases.referral.ReferralDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ReferralEndpoint(
    private val getAllReferralsByUserId: GetAllReferralsByUserId
) {
    @GetMapping("/user/{user_id}/referrals")
    fun getReferrals(@PathVariable("user_id") userId: Long): ResponseEntity<List<ReferralDTO>> {
        val referrals = getAllReferralsByUserId(userId)

        return ResponseEntity.ok()
            .body(referrals)
    }
}
