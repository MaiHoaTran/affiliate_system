package com.assignment.affiliate.api.http.v1.admin

import com.assignment.affiliate.api.http.v1.admin.dtos.UpdateCommissionRequest
import com.assignment.affiliate.domain.commission.toCommissionStatus
import com.assignment.affiliate.usecases.commission.CommissionDTO
import com.assignment.affiliate.usecases.commission.GetAllCommissionsByAffiliateId
import com.assignment.affiliate.usecases.commission.UpdateCommissionStatusByCommissionId
import com.assignment.affiliate.usecases.commission.UpdateCommissionStatusesByAffiliateId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminCommissionEndpoint(
    private val getAllCommissionsByAffiliateId: GetAllCommissionsByAffiliateId,
    private val updateCommissionStatusesByAffiliateId: UpdateCommissionStatusesByAffiliateId,
    private val updateCommissionStatusByCommissionId: UpdateCommissionStatusByCommissionId
) {
    @PatchMapping("/commission/{commission_id}")
    fun updateStatusByCommissionId(
        @PathVariable(name = "commission_id") id: Long,
        @RequestBody request: UpdateCommissionRequest
    ): ResponseEntity<Unit> {
        updateCommissionStatusByCommissionId(id, request.status.toCommissionStatus())

        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/affiliate/{affiliate_id}/commissions")
    fun updateStatusByAffiliateId(
        @PathVariable(name = "affiliate_id") affiliateId: Long,
        @RequestBody request: UpdateCommissionRequest
    ): ResponseEntity<Unit> {
        updateCommissionStatusesByAffiliateId(affiliateId, request.status.toCommissionStatus())

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/affiliate/{affiliate_id}/commissions")
    fun listAllCommissionByAffiliateId(@PathVariable(name = "affiliate_id") affiliateId: Long):
        ResponseEntity<List<CommissionDTO>> {
        return ResponseEntity.ok().body(getAllCommissionsByAffiliateId(affiliateId))
    }
}
