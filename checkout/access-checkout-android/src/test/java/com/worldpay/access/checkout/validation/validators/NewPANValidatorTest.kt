package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NewPANValidatorTest {

    private val panValidator = NewPANValidator()

    @Test
    fun `should return false and no card brand if pan is empty`() {
        assertFalse(panValidator.validate("", PAN_RULE))
    }

    @Test
    fun `should return false and null card brand if pan is unrecognised and shorter than default valid lengths`() {
        assertFalse(panValidator.validate("11111", PAN_RULE))
    }

    @Test
    fun `should return false and if unknown pan is valid length and an invalid luhn`() {
        assertFalse(panValidator.validate("1111111111111111", PAN_RULE))
    }

    @Test
    fun `should return true and if unrecognised pan is valid luhn and valid length`() {
        assertTrue(panValidator.validate("8888888888888888", PAN_RULE))
    }

    @Test
    fun `should return false and card brand if known pan is shorter than valid length`() {
        assertFalse(panValidator.validate("4111", VISA_BRAND.pan))
    }

    @Test
    fun `should return false and card brand if known pan is valid length but invalid luhn`() {
        assertFalse(panValidator.validate("44444444444444444", VISA_BRAND.pan))
    }

    @Test
    fun `should return true and card brand if known pan is valid length and valid luhn`() {
        assertTrue(panValidator.validate("4111111111111111", VISA_BRAND.pan))
    }

}
