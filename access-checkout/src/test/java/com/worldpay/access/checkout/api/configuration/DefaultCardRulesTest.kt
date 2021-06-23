package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVC_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.DEFAULT_MATCHER
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.EXPIRY_DATE_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.MONTH_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.YEAR_DEFAULTS
import kotlin.test.assertEquals
import org.junit.Test

class DefaultCardRulesTest {

    @Test
    fun `should return expected matcher for default matcher`() {
        assertEquals("^[0-9]*\$", DEFAULT_MATCHER)
    }

    @Test
    fun `should return expected pan defaults`() {
        val expected = CardValidationRule(
            matcher = DEFAULT_MATCHER,
            validLengths = listOf(12, 13, 14, 15, 16, 17, 18, 19)
        )

        assertEquals(expected, PAN_DEFAULTS)
    }

    @Test
    fun `should return expected cvc defaults`() {
        val expected = CardValidationRule(
            matcher = DEFAULT_MATCHER,
            validLengths = listOf(3, 4)
        )

        assertEquals(expected, CVC_DEFAULTS)
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
    fun `should return expected expiry date defaults`() {
        val expected = CardValidationRule(
            matcher = "^(0[1-9]|1[0-2])\\/([0-9][0-9])\$",
            validLengths = listOf(5)
        )

        assertEquals(expected, EXPIRY_DATE_DEFAULTS)
    }

    @Test
    fun `should return expected card defaults`() {
        val expected = CardDefaults(
            PAN_DEFAULTS,
            CVC_DEFAULTS,
            MONTH_DEFAULTS,
            YEAR_DEFAULTS,
            EXPIRY_DATE_DEFAULTS
        )

        assertEquals(expected, CARD_DEFAULTS)

        assertEquals(PAN_DEFAULTS, CARD_DEFAULTS.pan)
        assertEquals(CVC_DEFAULTS, CARD_DEFAULTS.cvc)
        assertEquals(MONTH_DEFAULTS, CARD_DEFAULTS.month)
        assertEquals(YEAR_DEFAULTS, CARD_DEFAULTS.year)
        assertEquals(EXPIRY_DATE_DEFAULTS, CARD_DEFAULTS.expiryDate)
    }
}
