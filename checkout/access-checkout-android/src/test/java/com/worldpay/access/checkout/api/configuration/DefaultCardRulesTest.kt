package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.DEFAULT_MATCHER
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.MONTH_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.YEAR_DEFAULTS
import org.junit.Test
import kotlin.test.assertEquals

class DefaultCardRulesTest {

    @Test
    fun `should return expected matcher for default matcher`() {
        assertEquals("^[0-9]*\$", DEFAULT_MATCHER)
    }

    @Test
    fun `should return expected pan defaults`() {
        val expected = CardValidationRule(
            matcher = DEFAULT_MATCHER,
            validLengths = listOf(15, 16, 18, 19)
        )

        assertEquals(expected, PAN_DEFAULTS)
    }

    @Test
    fun `should return expected cvv defaults`() {
        val expected = CardValidationRule(
            matcher = DEFAULT_MATCHER,
            validLengths = listOf(3, 4)
        )

        assertEquals(expected, CVV_DEFAULTS)
    }

    @Test
    fun `should return expected month defaults`() {
        val expected = CardValidationRule(
            matcher = "^0[1-9]{0,1}$|^1[0-2]{0,1}$",
            validLengths = listOf(2)
        )

        assertEquals(expected, MONTH_DEFAULTS)
    }

    @Test
    fun `should return expected year defaults`() {
        val expected = CardValidationRule(
            matcher = "^\\d{0,2}$",
            validLengths = listOf(2)
        )

        assertEquals(expected, YEAR_DEFAULTS)
    }

    @Test
    fun `should return expected card defaults`() {
        val expected = CardDefaults(
            PAN_DEFAULTS,
            CVV_DEFAULTS,
            MONTH_DEFAULTS,
            YEAR_DEFAULTS
        )

        assertEquals(expected, CARD_DEFAULTS)
    }

}