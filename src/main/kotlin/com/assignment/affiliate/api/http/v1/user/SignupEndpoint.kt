package com.assignment.affiliate.api.http.v1.user

import com.assignment.affiliate.api.http.v1.user.dtos.SignupRequest
import com.assignment.affiliate.usecases.signup.Signup
import com.assignment.affiliate.usecases.signup.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/signup")
class SignupEndpoint(
    private val signup: Signup
) {
    @PostMapping
    fun postSignup(@RequestBody request: SignupRequest): ResponseEntity<UserDTO> {
        val userDTO = signup(request.email, request.password, request.refCode)
        return ResponseEntity.ok()
            .body(userDTO)
    }
}
