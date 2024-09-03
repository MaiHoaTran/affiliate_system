package com.assignment.affiliate.api.http.v1.admin

import com.assignment.affiliate.api.http.v1.admin.dtos.CreateAdminUserRequest
import com.assignment.affiliate.usecases.user.CreateUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminUserEndpoint(
    private val createUser: CreateUser
) {
    @PostMapping("/users")
    fun createAdminUser(@RequestBody request: CreateAdminUserRequest): ResponseEntity<Unit> {
        createUser(request.email, request.password, true)

        return ResponseEntity.noContent().build()
    }
}
