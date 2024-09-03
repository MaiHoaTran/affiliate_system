package com.assignment.affiliate.configuration

import com.assignment.affiliate.domain.user.Role
import com.assignment.affiliate.infrastructure.authentication.JwtAuthenticationFilter
import com.assignment.affiliate.infrastructure.authentication.JwtTokenGeneratorFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtTokenGeneratorFilter: JwtTokenGeneratorFilter
) {
    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        return httpSecurity
            .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter::class.java)
            .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter::class.java)
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/admin/**").hasAnyRole(Role.ADMIN.name)
                    .requestMatchers("/api/v1/webhook/*", "/api/v1/signup").anonymous()
                    .anyRequest().hasAnyRole(Role.USER.name, Role.ADMIN.name)
            }
            .csrf { csrf -> csrf.disable() }
            .httpBasic { }
            .authenticationManager(authenticationManager)
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun userAuthenticationManager(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {
        val authenticationProvider = UsernamePwdAuthenticationProvider(userDetailsService, passwordEncoder)
        val providerManager = ProviderManager(authenticationProvider)
        providerManager.isEraseCredentialsAfterAuthentication = false
        return providerManager
    }
}
