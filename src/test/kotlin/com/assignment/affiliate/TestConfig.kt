package com.assignment.affiliate

import com.assignment.affiliate.domain.user.Role
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import javax.sql.DataSource

@TestConfiguration
internal class TestConfig {
    @Bean("postgres")
    @Primary
    fun postgresDataSource(): DataSource {
        return mock()
    }

    @Bean
    fun schedulerFactoryBean(): SchedulerFactoryBean {
        return mock()
    }

    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService {
        val admin: UserDetails = User.withUsername(ADMIN_EMAIL)
            .password(encoder.encode(ADMIN_PASSWORD))
            .roles(Role.ADMIN.name)
            .build()
        val user: UserDetails = User.withUsername(USER_EMAIL)
            .password(encoder.encode(USER_PASSWORD))
            .roles(Role.USER.name)
            .build()
        return InMemoryUserDetailsManager(admin, user)
    }

    companion object {
        const val ADMIN_EMAIL = "admin@test.com"
        const val ADMIN_PASSWORD = "password"
        const val USER_EMAIL = "user@test.com"
        const val USER_PASSWORD = "pwd1"
    }
}
