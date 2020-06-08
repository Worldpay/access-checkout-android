package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NewCVVValidatorTest {

    private val cvvValidator = NewCVVValidator()
    private val defaultRule = CardValidationRule(matcher = "^[0-9]*\$", validLengths = listOf(3))

    @Test
    fun `should return false if cvv is empty`() {
        assertFalse(cvvValidator.validate("", defaultRule))
    }

    @Test
    fun `should return false if cvv is invalid`() {
        assertFalse(cvvValidator.validate("aaa", defaultRule))
    }

    @Test
    fun `should return false if cvv is too short`() {
        assertFalse(cvvValidator.validate("11", defaultRule))
    }

    @Test
    fun `should return false if cvv is too long`() {
        assertFalse(cvvValidator.validate("1111", defaultRule))
    }

    @Test
    fun `should return true if cvv is valid length`() {
        assertTrue(cvvValidator.validate("111", defaultRule))
    }
}