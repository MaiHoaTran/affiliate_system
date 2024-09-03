package com.assignment.affiliate.usecases.subscription

data class InvalidPaypalPayloadEventException(val field: String) :
    RuntimeException("The payload field [$field] is missing/invalid")
