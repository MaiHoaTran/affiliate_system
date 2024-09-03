package com.assignment.affiliate.configuration

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsernamePwdAuthenticationProvider(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username: String = authentication.name
        val pwd: String = authentication.credentials.toString()
        val userDetails = userDetailsService.loadUserByUsername(username)
        if (passwordEncoder.matches(pwd, userDetails.password)) {
            return UsernamePasswordAuthenticationToken(username, pwd, userDetails.authorities)
        }
        throw BadCredentialsException("Invalid username or password")
    }

    override fun supports(authentication: Class<*>): Boolean {
        return (UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication))
    }
}
