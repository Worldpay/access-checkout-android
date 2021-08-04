package com.worldpay.access.checkout.validation.utils

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVC_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvcValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getMaxLength
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationUtil.isNumeric
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilTest {

    @Test
    fun `should be able to retrieve cvc validation rule given a brand`() {
        val rule = getCvcValidationRule(VISA_BRAND)
        assertEquals(VISA_BRAND.cvc, rule)
    }

    @Test
    fun `should be able to retrieve cvc validation rule given no brand`() {
        val rule = getCvcValidationRule(null)
        assertEquals(CVC_DEFAULTS, rule)
    }

    @Test
    fun `should be able to retrieve pan validation rule given card brand`() {
        val rule = getPanValidationRule(VISA_BRAND)
        assertEquals(VISA_BRAND.pan, rule)
    }

    @Test
    fun `should be able to retrieve pan validation rule given no card brand`() {
        val rule = getPanValidationRule(null)
        assertEquals(PAN_DEFAULTS, rule)
    }

    @Test
    fun `should return default max length when no valid lengths are found`() {
        val maxLength = getMaxLength(CardValidationRule("", emptyList()))
        assertEquals(100, maxLength)
    }

    @Test
    fun `should be able to find brand for pan`() {
        mockSuccessfulCardConfiguration()
        assertEquals(VISA_BRAND, findBrandForPan(visaPan()))
    }

    @Test
    fun `should be able to find brand for formatted pan`() {
        mockSuccessfulCardConfiguration()
        assertEquals(VISA_BRAND, findBrandForPan("4111 1111 1111 1111"))
    }

    @Test
    fun `should be able to find null brand for unrecognised pan`() {
        assertNull(findBrandForPan("0000"))
    }

    @Test
    fun `should be able to find null brand for empty pan`() {
        assertNull(findBrandForPan(""))
    }

    @Test
    fun `should be able to find null brand for visa pan but where card config has no brands`() {
        CardConfigurationProvider("", mock(), emptyList())
        assertNull(findBrandForPan(visaPan()))
    }

    @Test
    fun `given single numeric char then isNumeric should return true`() {
        assertTrue(isNumeric("1"))
    }

    @Test
    fun `given all numeric chars then isNumeric should return true`() {
        assertTrue(isNumeric("1234567890"))
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
