package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CVCValidationRuleProviderTest {

    private lateinit var cvcValidationRuleManager: CVCValidationRuleManager

    @Before
    fun setup() {
        cvcValidationRuleManager = CVCValidationRuleManager()
    }

    @Test
    fun `should start with cvv default`() {
        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())
    }

    @Test
    fun `should be able to update rule`() {
        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())

        cvcValidationRuleManager.updateRule(VISA_BRAND.cvv)

        assertEquals(VISA_BRAND.cvv, cvcValidationRuleManager.getRule())
    }

}
