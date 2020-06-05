package com.worldpay.access.checkout.validation

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.card.CardDetailType
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutCardValidationListener>()

    private lateinit var validationResultHandler: ValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = ValidationResultHandler(validationListener)
    }

    @Test
    fun `should call listener when pan is validated with no brand`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        validationResultHandler.handle(PAN, validationResult, null)

        verify(validationListener).onPanValidated(null, true)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when pan is validated with brand`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        validationResultHandler.handle(PAN, validationResult, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, true)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry month is validated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handle(EXPIRY_MONTH, validationResult)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry year is validated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handle(EXPIRY_YEAR, validationResult)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is validated with no brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handle(CVV, validationResult, null)

        verify(validationListener).onCvvValidated(null, true)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is validated with brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )

        validationResultHandler.handle(CVV, validationResult, VISA_BRAND)

        verify(validationListener).onCvvValidated(VISA_BRAND, true)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when pan is invalidated with no brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handle(PAN, validationResult, null)

        verify(validationListener).onPanValidated(null, false)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when pan is invalidated with brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handle(PAN, validationResult, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, false)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry month is invalidated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handle(EXPIRY_MONTH, validationResult)

        verify(validationListener).onExpiryDateValidated(false)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when expiry year is invalidated`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handle(EXPIRY_YEAR, validationResult)

        verify(validationListener).onExpiryDateValidated(false)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is invalidated with no brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handle(CVV, validationResult, null)

        verify(validationListener).onCvvValidated(null, false)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listener when cvv is invalidated with brand`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )

        validationResultHandler.handle(CVV, validationResult, VISA_BRAND)

        verify(validationListener).onCvvValidated(VISA_BRAND, false)
        verify(validationListener).onValidationFailure(any())
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call listeners validation failure function with all invalid fields`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = false
            )
        val argumentCaptor = argumentCaptor<List<CardDetailType>>()

        validationResultHandler.handle(PAN, validationResult, null)

        verify(validationListener).onPanValidated(null, false)
        verify(validationListener).onValidationFailure(argumentCaptor.capture())
        verifyNoMoreInteractions(validationListener)

        assertEquals(1, argumentCaptor.allValues.size)
        assertEquals(4, argumentCaptor.firstValue.size)
        assertEquals(listOf(PAN, EXPIRY_MONTH, EXPIRY_YEAR, CVV), argumentCaptor.firstValue)
    }

    @Test
    fun `should call listeners validation failure function with all other invalid fields where pan is valid`() {
        val validationResult =
            ValidationResult(
                partial = true,
                complete = true
            )
        val argumentCaptor = argumentCaptor<List<CardDetailType>>()

        validationResultHandler.handle(PAN, validationResult, null)

        verify(validationListener).onPanValidated(null, true)
        verify(validationListener).onValidationFailure(argumentCaptor.capture())
        verifyNoMoreInteractions(validationListener)

        assertEquals(1, argumentCaptor.allValues.size)
        assertEquals(3, argumentCaptor.firstValue.size)
        assertEquals(listOf(EXPIRY_MONTH, EXPIRY_YEAR, CVV), argumentCaptor.firstValue)
    }

    @Test
    fun `should call listeners validation success function when all fields are valid`() {
        val validationResult = ValidationResult(partial = true, complete = true)
        val argumentCaptor = argumentCaptor<List<CardDetailType>>()

        // validate the pan
        validationResultHandler.handle(PAN, validationResult, null)
        verify(validationListener).onPanValidated(null, true)

        // validate the expiry month
        validationResultHandler.handle(EXPIRY_MONTH, validationResult, null)
        verify(validationListener, times(1)).onExpiryDateValidated(true)

        // validate the expiry year
        validationResultHandler.handle(EXPIRY_YEAR, validationResult, null)
        verify(validationListener, times(2)).onExpiryDateValidated(true)

        // validate the cvv
        validationResultHandler.handle(CVV, validationResult, null)
        verify(validationListener).onCvvValidated(null, true)

        // ensure validation failure has been called 3 times
        verify(validationListener, times(3)).onValidationFailure(argumentCaptor.capture())

        // ensure validation success only got called 1 time
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)


        // check the number of times arguments were captured
        // should be same as number of times validation failure was called
        assertEquals(3, argumentCaptor.allValues.size)

        // validate the first set of arguments to the validation failure call
        assertEquals(listOf(EXPIRY_MONTH, EXPIRY_YEAR, CVV), argumentCaptor.firstValue)

        // validate the second set of arguments to the validation failure call
        assertEquals(listOf(EXPIRY_YEAR, CVV), argumentCaptor.secondValue)

        // validate the third set of arguments to the validation failure call
        assertEquals(listOf(CVV), argumentCaptor.thirdValue)
    }

}