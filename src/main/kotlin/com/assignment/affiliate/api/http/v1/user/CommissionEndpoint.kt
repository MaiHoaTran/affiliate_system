package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.api.http.v1.admin.dtos.TotalCommissionResponse
import com.assignment.affiliate.domain.commission.toCommissionStatus
import com.assignment.affiliate.usecases.commission.CalculateCommissionAmountByUserIdAndStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class CommissionEndpoint(
    private val calculateCommissionAmountByUserIdAndStatus: CalculateCommissionAmountByUserIdAndStatus
) {
    @GetMapping("/user/{user_id}/commissions/total")
    fun getTotalCommissionAmount(
        @PathVariable(name = "user_id") userId: Long,
        @RequestParam(value = "status") status: String
    ): ResponseEntity<TotalCommissionResponse> {
        val totalAmount = calculateCommissionAmountByUserIdAndStatus(userId, status.toCommissionStatus())

        return ResponseEntity.ok().body(
            TotalCommissionResponse(
                totalCommissionAmount = totalAmount
            )
        )
    }
}
