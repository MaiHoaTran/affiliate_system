package com.assignment.affiliate.usecases.signup

import com.assignment.affiliate.usecases.affiliate.CreateAffiliate
import com.assignment.affiliate.usecases.referral.CreateReferral
import com.assignment.affiliate.usecases.user.CreateUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Use case to sign up:
 * - Mocked process when a user is signed up
 * - Create affiliate with created user
 * - Create referral with created user and referral code
 */
@Service
@Transactional
class Signup(
    private val createUser: CreateUser,
    private val createAffiliate: CreateAffiliate,
    private val createReferral: CreateReferral
) {
    /**
     * @param email the user's email
     * @param password the user's password
     * @param refCode the referral code
     * @return [UserDTO]
     */
    operator fun invoke(email: String, password: String, refCode: String?): UserDTO {
        val user = createUser(email, password)

        val affiliate = createAffiliate(user.id!!)
        refCode?.let { createReferral(user.id, refCode) }

        return UserDTO(
            email = user.email,
            affiliateCode = affiliate.affiliateCode
        )
    }
}
