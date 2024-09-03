package com.assignment.affiliate.infrastructure.authentication

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class JwtTokenGeneratorFilter(
    @Value("\${spring.security.jwt.secret}")
    val jwtSecret: String,
    val clock: Clock
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null) {
            val secretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
            val jwt = Jwts.builder()
                .claim("username", authentication.name)
                .claim(
                    "authorities",
                    authentication.authorities.map { GrantedAuthority::getAuthority }.joinToString(",")
                )
                .issuedAt(Date.from(Instant.now(clock)))
                .expiration(Date.from(Instant.now(clock).plus(8, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact()
            response.addHeader("Authorization", "Bearer $jwt")
        }
        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.servletPath.equals("/v1/api/users/me")
    }
}
