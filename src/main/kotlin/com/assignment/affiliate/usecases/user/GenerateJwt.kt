package com.assignment.affiliate.usecases.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

/**
 * Use case to generate JWT by given authentication
 */
@Service
class GenerateJwt(
    private val clock: Clock,
    @Value("\${spring.security.jwt.secret}")
    val jwtSecret: String
) {
    /**
     * @param authentication the given [Authentication]
     * @return the jwt
     */
    operator fun invoke(authentication: Authentication): String {
        val secretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val authorities = authentication.authorities.joinToString(",") { it.authority }

        return Jwts.builder()
            .claim("username", authentication.name)
            .claim("authorities", authorities)
            .issuedAt(Date.from(Instant.now(clock)))
            .expiration(Date.from(Instant.now(clock).plus(8, ChronoUnit.HOURS)))
            .signWith(secretKey)
            .compact()
    }
}
