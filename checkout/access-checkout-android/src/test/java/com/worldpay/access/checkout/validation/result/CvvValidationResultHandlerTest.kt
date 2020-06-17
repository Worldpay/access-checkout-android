package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvvValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutCvvValidationListener>()
    private val validationStateManager = CardValidationStateManager()

    private lateinit var validationResultHandler: CvvValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = CvvValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when cvc is valid and was previously invalid`() {
        validationResultHandler.handleResult(false)

        validationResultHandler.handleResult(true)

        verify(validationListener).onCvvValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should call listener when cvc is invalid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verify(validationListener).onCvvValidated( false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should not call listener when cvc is valid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(true)

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should not call listener when cvc is invalid and was previously invalid`() {
        validationResultHandler.handleResult(false)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verifyZeroInteractions(validationListener)

        assertFalse(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should call listener when focus is changed and notification has not been sent previously`() {
        validationResultHandler.handleFocusChange()

        verify(validationListener).onCvvValidated(false)

        assertFalse(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should not call listener when focus is changed and notification has been sent previously`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleFocusChange()

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationResult = true

        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.cvcValidationState).willReturn(false)

        val validationResultHandler = CvvValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvvValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
