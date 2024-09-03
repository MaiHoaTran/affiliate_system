package com.assignment.affiliate.usecases.user

import com.assignment.affiliate.domain.exception.DuplicateUserException
import com.assignment.affiliate.domain.user.User
import com.assignment.affiliate.domain.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

/**
 * Use case to create new user wth given email and password
 */
@Service
@Transactional
class CreateUser(
    private val clock: Clock,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * @param email the user's email
     * @param password the user's password
     */
    operator fun invoke(email: String, password: String, isAdmin: Boolean = false): User {
        userRepository.findByEmail(email)?.let { throw DuplicateUserException() }

        val user = User(
            email = email,
            password = passwordEncoder.encode(password),
            isAdmin = isAdmin,
            createdAt = clock.instant()
        )

        return userRepository.upsert(user)
    }
}
