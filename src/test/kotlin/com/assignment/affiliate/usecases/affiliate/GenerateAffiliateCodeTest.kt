package com.assignment.affiliate.usecases.affiliate

import com.assignment.affiliate.domain.affiliate.Affiliate.Companion.AFFILIATE_CODE_LENGTH
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GenerateAffiliateCodeTest {
    private val generateAffiliateCode = GenerateAffiliateCode()

    @Test
    fun `should generate affiliate code with correct length and containing only letters and digits`() {
        val code = generateAffiliateCode()

        assertEquals(AFFILIATE_CODE_LENGTH, code.length)
        assertTrue(code.all { it.isLetterOrDigit() }, "Affiliate code should only contain letters and digits")
    }
}
