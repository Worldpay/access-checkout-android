package com.worldpay.access.checkout.validation.utils

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import org.junit.Test
import kotlin.test.assertEquals

class ValidationRuleHelperTest {

    @Test
    fun `should be able to retrieve cvv validation rule given a pan`() {
        val rule = ValidationRuleHelper.getCvvValidationRule(VISA_PAN, CARD_CONFIG_BASIC)
        assertEquals(VISA_BRAND.cvv, rule)
    }

    @Test
    fun `should be able to retrieve cvv validation rule given no pan`() {
        val rule = ValidationRuleHelper.getCvvValidationRule("", CARD_CONFIG_BASIC)
        assertEquals(CVV_DEFAULTS, rule)
    }

    @Test
    fun `should be able to retrieve cvv validation rule given pan but no brand`() {
        val rule = ValidationRuleHelper.getCvvValidationRule(VISA_PAN, CARD_CONFIG_NO_BRAND)
        assertEquals(CVV_DEFAULTS, rule)
    }

    @Test
    fun `should be able to retrieve pan validation rule given card brand`() {
        val rule = ValidationRuleHelper.getPanValidationRule(VISA_BRAND, CARD_CONFIG_BASIC)
        assertEquals(VISA_BRAND.pan, rule)
    }

    @Test
    fun `should be able to retrieve pan validation rule given no card brand`() {
        val rule = ValidationRuleHelper.getPanValidationRule(null, CARD_CONFIG_BASIC)
        assertEquals(PAN_DEFAULTS, rule)
    }

    @Test
    fun `should return default max length when no valid lengths are found`() {
        val maxLength = ValidationRuleHelper.getMaxLength(CardValidationRule("", emptyList()))
        assertEquals(100, maxLength)
    }

    @Test
    fun `should return max length when valid lengths are found`() {
        val maxLength = ValidationRuleHelper.getMaxLength(CardValidationRule("", listOf(10, 11)))
        assertEquals(11, maxLength)
    }

}
