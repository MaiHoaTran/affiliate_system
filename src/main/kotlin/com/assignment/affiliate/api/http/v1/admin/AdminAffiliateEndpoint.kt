package com.assignment.affiliate.api.http.v1.admin

import com.assignment.affiliate.usecases.affiliate.AffiliateDTO
import com.assignment.affiliate.usecases.affiliate.GetAllAffiliates
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminAffiliateEndpoint(
    private val getAllAffiliates: GetAllAffiliates
) {
    @GetMapping("/affiliates")
    fun getAffiliates(): ResponseEntity<List<AffiliateDTO>> {
        return ResponseEntity.ok(getAllAffiliates())
    }
}
