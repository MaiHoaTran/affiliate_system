package com.assignment.affiliate.domain.affiliate

import com.assignment.affiliate.usecases.affiliate.AffiliateDTO

/**
 * Affiliate repository performs data operations related to affiliates table
 */
interface AffiliateRepository {
    /**
     * Find all affiliates
     *
     * @return the list of [AffiliateDTO]
     */
    fun findAll(): List<AffiliateDTO>

    /**
     * Find affiliate by user id
     *
     * @param userId the given user id
     * @return [Affiliate] or null
     */
    fun findByUserId(userId: Long): Affiliate?

    /**
     * Find affiliate by affiliate's code
     *
     * @param affiliateCode the given affiliate's code
     * @return [Affiliate] or null
     */
    fun findByAffiliateCode(affiliateCode: String): Affiliate?

    /**
     * Insert the given affiliate
     *
     * @param affiliate the affiliate to be inserted
     * @return [Affiliate]
     * @throws DuplicateAffiliateUserException if there exists any affiliate with the same user id
     * @throws DuplicateAffiliateCodeException if there exists any affiliate with the same affiliate code
     */
    fun save(affiliate: Affiliate): Affiliate
}
