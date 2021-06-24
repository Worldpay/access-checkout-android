package com.worldpay.access.checkout.validation.validators

import com.nhaarman.mockitokotlin2.reset
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.result.handler.CvcValidationResultHandler
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.verify
import org.mockito.Mockito.mock

class CvcValidatorTest {

    private val cvcValidationResultHandler = mock(CvcValidationResultHandler::class.java)

    private lateinit var cvcValidator: CvcValidator
    private lateinit var cvcValidationRuleManager: CVCValidationRuleManager

    @Before
    fun setup() {
        cvcValidationRuleManager = CVCValidationRuleManager()

        cvcValidator = CvcValidator(
            cvcValidationResultHandler = cvcValidationResultHandler,
            cardValidationRuleProvider = cvcValidationRuleManager
        )
    }

    @Test
    fun `should validate cvc using the default validation rule`() {
        cvcValidator.validate("123")
        verify(cvcValidationResultHandler).handleResult(true)
        reset(cvcValidationResultHandler)

        cvcValidator.validate("1234")
        verify(cvcValidationResultHandler).handleResult(true)
        reset(cvcValidationResultHandler)

        cvcValidator.validate("12345")
        verify(cvcValidationResultHandler).handleResult(false)
        reset(cvcValidationResultHandler)
    }

    @Test
    fun `should validate cvc rule with new rule and use that rule thereafter`() {
        cvcValidator.validate("123")
        verify(cvcValidationResultHandler).handleResult(true)
        reset(cvcValidationResultHandler)

        cvcValidationRuleManager.updateRule(VISA_BRAND.cvc)

        cvcValidator.validate("1234")
        verify(cvcValidationResultHandler).handleResult(false)
        reset(cvcValidationResultHandler)

        cvcValidator.validate("1234")
        verify(cvcValidationResultHandler).handleResult(false)
        reset(cvcValidationResultHandler)
    }
}
