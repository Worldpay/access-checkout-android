package com.worldpay.access.checkout.validation.validators

import com.nhaarman.mockitokotlin2.reset
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.verify
import org.mockito.Mockito.mock

class CVCValidatorTest {

    private val cvcValidationResultHandler = mock(CvvValidationResultHandler::class.java)

    private lateinit var cvcValidator: CVCValidator

    @Before
    fun setup() {
        cvcValidator = CVCValidator(cvcValidationResultHandler)
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

        cvcValidator.validate("1234", VISA_BRAND.cvv)
        verify(cvcValidationResultHandler).handleResult(false)
        reset(cvcValidationResultHandler)

        cvcValidator.validate("1234")
        verify(cvcValidationResultHandler).handleResult(false)
        reset(cvcValidationResultHandler)
    }

}
