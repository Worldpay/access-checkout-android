package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.CARD_BRAND_NOT_ACCEPTED
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.INVALID
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.INVALID_LUHN
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.VALID
import kotlin.test.assertEquals
import org.junit.Test

class PanValidatorTest {

    private val panValidator = PanValidator(emptyArray())

    @Test
    fun `should return INVALID if pan is empty`() {
        assertEquals(INVALID, panValidator.validate("", PAN_RULE, null))
    }

    @Test
    fun `should return INVALID if pan is unrecognised and shorter than default valid lengths`() {
        assertEquals(INVALID, panValidator.validate("11111", PAN_RULE, null))
    }

    @Test
    fun `should return INVALID_LUHN if unknown pan is valid length and an invalid luhn`() {
        assertEquals(INVALID_LUHN, panValidator.validate(INVALID_UNKNOWN_LUHN, PAN_RULE, null))
    }

    @Test
    fun `should return VALID if unrecognised pan is valid luhn and valid length`() {
        assertEquals(VALID, panValidator.validate(VALID_UNKNOWN_LUHN, PAN_RULE, null))
    }

    @Test
    fun `should return INVALID if known pan is shorter than valid length`() {
        assertEquals(INVALID, panValidator.validate("4111", VISA_BRAND.pan, VISA_BRAND))
    }

    @Test
    fun `should return VALID if known pan is valid length and valid luhn`() {
        assertEquals(VALID, panValidator.validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND))
    }

    @Test
    fun `should return VALID if known pan is valid length and valid luhn and is formatted`() {
        assertEquals(VALID, panValidator.validate("4111 1111 1111 1111", VISA_BRAND.pan, VISA_BRAND))
    }

    @Test
    fun `should return CARD_BRAND_NOT_ACCEPTED if the pan is not one of the accepted card brands`() {
        val panValidator = PanValidator(arrayOf("MASTERCARD"))
        assertEquals(CARD_BRAND_NOT_ACCEPTED, panValidator.validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND))
    }

    @Test
    fun `should return VALID if the pan is of one of the accepted card brands and everything else is valid`() {
        val panValidator = PanValidator(arrayOf("VISA", "MASTERCARD"))
        assertEquals(VALID, panValidator.validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND))
    }

    @Test
    fun `should return VALID if the pan is of one of the accepted card brands and everything else is valid - ignore case`() {
        val panValidator = PanValidator(arrayOf("VisA", "MASTERCARD"))
        assertEquals(VALID, panValidator.validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND))
    }
}
