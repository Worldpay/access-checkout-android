package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.AccessCheckoutExpiryDateValidatedSuccessListener
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpiryYearValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutExpiryDateValidatedSuccessListener>()
    private val validationStateManager = ValidationStateManager()

    private lateinit var validationResultHandler: ExpiryYearValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = ExpiryYearValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when cvv is valid`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onExpiryDateValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.yearValidated.get())
    }

    @Test
    fun `should call listener when cvv is invalid`() {
        val validationResult = ValidationResult(partial = true, complete = false)

        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onExpiryDateValidated(false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.yearValidated.get())
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        val validationStateManager = mock<ValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.yearValidated).willReturn(AtomicBoolean(false))

        val validationResultHandler = ExpiryYearValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}