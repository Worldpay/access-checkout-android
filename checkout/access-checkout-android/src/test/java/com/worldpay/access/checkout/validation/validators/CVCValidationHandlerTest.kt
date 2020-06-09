package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.watchers.CVCValidationHandler
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class CVCValidationHandlerTest {

    private val cvcValidator = mock(SimpleValidator::class.java)
    private val cvcValidationResultHandler = mock(CvvValidationResultHandler::class.java)
    private val validationRule = DefaultCardRules.CVV_DEFAULTS
    private val result = true
    private val cvc = "123"

    private val cvcValidationHandler = CVCValidationHandler(cvcValidator, cvcValidationResultHandler)

    @Before
    fun setup() {
    }

    @Test
    fun `should validate cvc the pass result to handler`() {
        given(cvcValidator.validate(cvc, validationRule)).willReturn(result)

        cvcValidationHandler.validate(cvc)

        verify(cvcValidator).validate(cvc, validationRule)
        verify(cvcValidationResultHandler).handleResult(result)
    }

    @Test
    fun `should update cvc rule`() {
        cvcValidationHandler.validate("1234")
        verify(cvcValidationResultHandler).handleResult(true)

        cvcValidationHandler.updateValidationRule(VISA_BRAND.cvv)

        cvcValidationHandler.validate("1234")
        verify(cvcValidationResultHandler).handleResult(false)
    }

}
