package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PanValidatorTest {

    private val panValidator = PanValidator(emptyArray())

    @Test
    fun `should return false if pan is empty`() {
        assertFalse(panValidator.validate("", PAN_RULE, null))
    }

    @Test
    fun `should return false if pan is unrecognised and shorter than default valid lengths`() {
        assertFalse(panValidator.validate("11111", PAN_RULE, null))
    }

    @Test
    fun `should return false if unknown pan is valid length and an invalid luhn`() {
        assertFalse(panValidator.validate(INVALID_UNKNOWN_LUHN, PAN_RULE, null))
    }

    @Test
    fun `should return true if unrecognised pan is valid luhn and valid length`() {
        assertTrue(panValidator.validate(VALID_UNKNOWN_LUHN, PAN_RULE, null))
    }

    @Test
    fun `should return false if known pan is shorter than valid length`() {
        assertFalse(panValidator.validate("4111", VISA_BRAND.pan, null))
    }

    @Test
    fun `should return false if known pan is valid length but invalid luhn`() {
        assertFalse(panValidator.validate("44444444444444444", VISA_BRAND.pan, null))
    }

    @Test
    fun `should return true if known pan is valid length and valid luhn`() {
        assertTrue(panValidator.validate(VISA_PAN, VISA_BRAND.pan, null))
    }

    @Test
    fun `should return false if the pan is not one of the accepted card brands`() {
        val panValidator = PanValidator(arrayOf("MASTERCARD"))
        assertFalse(panValidator.validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND))
    }

    @Test
    fun `should return true if the pan is of one of the accepted card brands and everything else is valid`() {
        val panValidator = PanValidator(arrayOf("VISA", "MASTERCARD"))
        assertTrue(panValidator.validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND))
    }

    @Test
    fun `should return true if the pan is of one of the accepted card brands and everything else is valid - ignore case`() {
        val panValidator = PanValidator(arrayOf("VisA", "MASTERCARD"))
        assertTrue(panValidator.validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND))
    }

}
