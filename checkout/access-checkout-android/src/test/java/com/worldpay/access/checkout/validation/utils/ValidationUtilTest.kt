package com.worldpay.access.checkout.validation.utils

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.utils.ValidationUtil.isNumeric
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidationUtilTest {

    @Test
    fun `should be able to retrieve cvv validation rule given a brand`() {
        val rule = ValidationUtil.getCvvValidationRule(VISA_BRAND, CARD_CONFIG_BASIC)
        assertEquals(VISA_BRAND.cvv, rule)
    }

    @Test
    fun `should be able to retrieve cvv validation rule given no brand`() {
        val rule = ValidationUtil.getCvvValidationRule(null, CARD_CONFIG_BASIC)
        assertEquals(CVV_DEFAULTS, rule)
    }

    @Test
    fun `should be able to retrieve pan validation rule given card brand`() {
        val rule = ValidationUtil.getPanValidationRule(VISA_BRAND, CARD_CONFIG_BASIC)
        assertEquals(VISA_BRAND.pan, rule)
    }

    @Test
    fun `should be able to retrieve pan validation rule given no card brand`() {
        val rule = ValidationUtil.getPanValidationRule(null, CARD_CONFIG_BASIC)
        assertEquals(PAN_DEFAULTS, rule)
    }

    @Test
    fun `should return default max length when no valid lengths are found`() {
        val maxLength = ValidationUtil.getMaxLength(CardValidationRule("", emptyList()))
        assertEquals(100, maxLength)
    }

    @Test
    fun `should return max length when valid lengths are found`() {
        val maxLength = ValidationUtil.getMaxLength(CardValidationRule("", listOf(10, 11)))
        assertEquals(11, maxLength)
    }

    @Test
    fun `should be able to find brand for pan`() {
        assertEquals(VISA_BRAND, ValidationUtil.findBrandForPan(CARD_CONFIG_BASIC, VISA_PAN))
    }

    @Test
    fun `should be able to find null brand for unrecognised pan`() {
        assertNull(ValidationUtil.findBrandForPan(CARD_CONFIG_BASIC, "0000"))
    }

    @Test
    fun `should be able to find null brand for empty pan`() {
        assertNull(ValidationUtil.findBrandForPan(CARD_CONFIG_BASIC, ""))
    }

    @Test
    fun `should be able to find null brand for visa pan but where card config has no brands`() {
        assertNull(ValidationUtil.findBrandForPan(CARD_CONFIG_NO_BRAND, VISA_PAN))
    }

    @Test
    fun `given single numeric char then isNumeric should return true`() {
        Assert.assertTrue(isNumeric("1"))
    }

    @Test
    fun `given all numeric chars then isNumeric should return true`() {
        Assert.assertTrue(isNumeric("1234567890"))
    }

    @Test
    fun `given all non-numeric chars then isNumeric should return false`() {
        assertFalse(isNumeric("ABCDEFGHI"))
    }

    @Test
    fun `given some non-numeric chars then isNumeric should return false`() {
        assertFalse(isNumeric("123ABC456"))
    }

    @Test
    fun `given empty then isNumeric should return false`() {
        assertFalse(isNumeric(""))
    }

    @Test
    fun `given special chars then isNumeric should return false`() {
        assertFalse(isNumeric("@£%&*!"))
    }

    @Test
    fun `given single numeric char with a leading space then isNumeric should return false`() {
        assertFalse(isNumeric(" 1"))
    }

    @Test
    fun `given all numeric chars with a leading space then isNumeric should return false`() {
        assertFalse(isNumeric(" 123"))
    }

    @Test
    fun `given single numeric char with a trailing space then isNumeric should return false`() {
        assertFalse(isNumeric("1 "))
    }

    @Test
    fun `given all numeric chars with a trailing space then isNumeric should return false`() {
        assertFalse(isNumeric("123 "))
    }

    @Test
    fun `given all numeric chars with a space in the middle then isNumeric should return false`() {
        assertFalse(isNumeric("1 4"))
    }

}
