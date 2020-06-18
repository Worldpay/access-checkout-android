package com.worldpay.access.checkout.validation.result.handler

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpiryDateValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutExpiryDateValidationListener>()
    private val validationStateManager = CardValidationStateManager()

    private lateinit var validationResultHandler: ExpiryDateValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = ExpiryDateValidationResultHandler(
            validationListener,
            validationStateManager
        )
    }

    @Test
    fun `should call listener when expiry date is valid and was previously invalid`() {
        validationResultHandler.handleResult(false)

        validationResultHandler.handleResult(true)

        verify(validationListener).onExpiryDateValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.expiryDateValidationState)
    }

    @Test
    fun `should call listener when expiry date is invalid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verify(validationListener).onExpiryDateValidated( false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.expiryDateValidationState)
    }

    @Test
    fun `should not call listener when expiry date is valid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(true)

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.expiryDateValidationState)
    }

    @Test
    fun `should not call listener when expiry date is invalid and was previously invalid`() {
        validationResultHandler.handleResult(false)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verifyZeroInteractions(validationListener)

        assertFalse(validationStateManager.expiryDateValidationState)
    }

    @Test
    fun `should call listener when focus is changed and notification has not been sent previously`() {
        validationResultHandler.handleFocusChange()

        verify(validationListener).onExpiryDateValidated(false)

        assertFalse(validationStateManager.expiryDateValidationState)
    }

    @Test
    fun `should not call listener when focus is changed and notification has been sent previously`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleFocusChange()

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.expiryDateValidationState)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.expiryDateValidationState).willReturn(false)

        val validationResultHandler = ExpiryDateValidationResultHandler(
            validationListener,
            validationStateManager
        )
        validationResultHandler.handleResult(true)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
