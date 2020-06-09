package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleValidatorTest {

    private val simpleValidator = SimpleValidator()
    private val defaultRule = CardValidationRule(matcher = "^[0-9]*\$", validLengths = listOf(3))

    @Test
    fun `should return invalid result is text is empty`() {
        assertFalse(simpleValidator.validate("", defaultRule))
    }

    @Test
    fun `should return invalid result if text does not meet matcher`() {
        assertFalse(simpleValidator.validate("A", defaultRule))
    }

    @Test
    fun `should return valid result if text meets matcher and no valid lengths specified`() {
        val defaultRule = CardValidationRule(matcher = "^[0-9]*\$", validLengths = emptyList())
        assertTrue(simpleValidator.validate("123456789", defaultRule))
    }

    @Test
    fun `should return valid result if text meets matcher and valid lengths`() {
        assertTrue(simpleValidator.validate("123", defaultRule))
    }

    @Test
    fun `should return invalid result if text meets matcher but not valid lengths`() {
        assertFalse(simpleValidator.validate("1234", defaultRule))
    }

}
