package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.usecases.user.GenerateJwt
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserEndpoint(
    private val generateJwt: GenerateJwt
) {
    @PostMapping("/users/jwt")
    fun generateToken(authentication: Authentication): ResponseEntity<String> {
        val token = generateJwt(authentication)

        return ResponseEntity.ok(token)
    }
}
