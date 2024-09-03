package com.assignment.affiliate.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class User(
    val id: Long? = null,
    val email: String,
    @JsonIgnore
    val password: String,
    val isAdmin: Boolean = false,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

enum class Role {
    ADMIN,
    USER
}
