package com.assignment.affiliate.api.http.v1.admin.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TotalCommissionResponse(val totalCommissionAmount: Double)
