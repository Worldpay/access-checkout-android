package com.worldpay.access.checkout.validation.result.handler

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvcValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutCvcValidationListener>()
    private val validationStateManager = CardValidationStateManager()

    private lateinit var validationResultHandler: CvcValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = CvcValidationResultHandler(
            validationListener,
            validationStateManager
        )
    }

    @Test
    fun `should call listener when cvc is valid and was previously invalid`() {
        validationResultHandler.handleResult(false)

        validationResultHandler.handleResult(true)

        verify(validationListener).onCvcValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should call listener when cvc is invalid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verify(validationListener).onCvcValidated( false)
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

        verify(validationListener).onCvcValidated(false)

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

        val validationResultHandler = CvcValidationResultHandler(
            validationListener,
            validationStateManager
        )
        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvcValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
