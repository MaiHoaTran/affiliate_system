package com.assignment.affiliate.domain.user

/**
 * User repository performs database operations related to users table
 */
interface UserRepository {
    /**
     * Find user by id
     *
     * @param id the user ID
     * @return [User] or null
     */
    fun findById(id: Long): User?

    /**
     * Find user by email
     *
     * @param email the user's email
     * @return [User] or null
     */
    fun findByEmail(email: String): User?

    /**
     * Insert/Update the given user
     *
     * @param user the user to be inserted/updated
     * @return [User]
     */
    fun upsert(user: User): User
}
