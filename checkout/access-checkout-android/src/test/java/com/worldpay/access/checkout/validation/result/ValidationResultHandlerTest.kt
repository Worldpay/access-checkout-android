package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Before
import org.junit.Test

class ValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutCardValidationListener>()

    private lateinit var validationResultHandler: ValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler =
            ValidationResultHandler(
                validationListener
            )
    }

    @Test
    fun `should call listener when pan is validated with no brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handlePanValidationResult(validationResult, null)

        verify(validationListener).onPanValidated(null, true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when pan is validated with brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handlePanValidationResult(validationResult, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry month is validated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handleExpiryMonthValidationResult(validationResult)

        verify(validationListener).onExpiryDateValidated(true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry year is validated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handleExpiryYearValidationResult(validationResult)

        verify(validationListener).onExpiryDateValidated(true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is validated with no brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handleCvvValidationResult(validationResult)

        verify(validationListener).onCvvValidated(true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is validated with brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handleCvvValidationResult(validationResult)

        verify(validationListener).onCvvValidated(true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when pan is invalidated with no brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handlePanValidationResult(validationResult, null)

        verify(validationListener).onPanValidated(null, false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when pan is invalidated with brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handlePanValidationResult(validationResult, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry month is invalidated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handleExpiryMonthValidationResult(validationResult)

        verify(validationListener).onExpiryDateValidated(false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry year is invalidated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handleExpiryYearValidationResult(validationResult)

        verify(validationListener).onExpiryDateValidated(false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is invalidated with no brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handleCvvValidationResult(validationResult)

        verify(validationListener).onCvvValidated(false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is invalidated with brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handleCvvValidationResult(validationResult)

        verify(validationListener).onCvvValidated(false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listeners validation success function when all fields are valid`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        // validate the pan
        validationResultHandler.handlePanValidationResult(validationResult, null)
        verify(validationListener).onPanValidated(null, true)

        // validate the expiry month
        validationResultHandler.handleExpiryMonthValidationResult(validationResult)
        verify(validationListener, times(1)).onExpiryDateValidated(true)

        // validate the expiry year
        validationResultHandler.handleExpiryYearValidationResult(validationResult)
        verify(validationListener, times(2)).onExpiryDateValidated(true)

        // validate the cvv
        validationResultHandler.handleCvvValidationResult(validationResult)
        verify(validationListener).onCvvValidated(true)

        // ensure validation success only got called 1 time
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}