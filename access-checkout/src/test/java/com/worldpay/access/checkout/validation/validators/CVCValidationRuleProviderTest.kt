package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVC_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test

class CVCValidationRuleProviderTest {

    private lateinit var cvcValidationRuleManager: CVCValidationRuleManager

    @Before
    fun setup() {
        cvcValidationRuleManager = CVCValidationRuleManager()
    }

    @Test
    fun `should start with cvc default`() {
        assertEquals(CVC_DEFAULTS, cvcValidationRuleManager.getRule())
    }

    @Test
    fun `should be able to update rule`() {
        assertEquals(CVC_DEFAULTS, cvcValidationRuleManager.getRule())

        cvcValidationRuleManager.updateRule(VISA_BRAND.cvc)

        assertEquals(VISA_BRAND.cvc, cvcValidationRuleManager.getRule())
    }
}
