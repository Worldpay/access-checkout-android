package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.result.state.FieldValidationState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpiryDateValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutExpiryDateValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()
    private val validationStateManager = CardValidationStateManager
    private val fieldValidationState = mock<FieldValidationState>()

    private lateinit var validationResultHandler: ExpiryDateValidationResultHandler

    @Before
    fun setup() {
        validationStateManager.expiryDateValidationState.validationState = false
        validationStateManager.expiryDateValidationState.notificationSent = false

        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        validationResultHandler = ExpiryDateValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )
    }

    @Test
    fun `should call listener when expiry date is valid and was previously invalid and set notification sent state`() {
        validationResultHandler.handleResult(false)

        validationResultHandler.handleResult(true)

        verify(validationListener).onExpiryDateValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.expiryDateValidationState.validationState)
        assertTrue(validationStateManager.expiryDateValidationState.notificationSent)

    }

    @Test
    fun `should call listener when expiry date is invalid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verify(validationListener).onExpiryDateValidated( false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.expiryDateValidationState.validationState)
        assertTrue(validationStateManager.expiryDateValidationState.notificationSent)
    }

    @Test
    fun `should not call listener when expiry date is valid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(true)

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.expiryDateValidationState.validationState)
    }

    @Test
    fun `should not call listener when expiry date is invalid and was previously invalid`() {
        validationResultHandler.handleResult(false)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verifyZeroInteractions(validationListener)

        assertFalse(validationStateManager.expiryDateValidationState.validationState)
    }

    @Test
    fun `should call listener when focus is changed and notification has not been sent previously`() {
        validationResultHandler.handleFocusChange()

        verify(validationListener).onExpiryDateValidated(false)

        assertFalse(validationStateManager.expiryDateValidationState.validationState)
        assertTrue(validationStateManager.expiryDateValidationState.notificationSent)
    }

    @Test
    fun `should not call listener when focus is changed and notification has been sent previously`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleFocusChange()

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.expiryDateValidationState.validationState)
        assertTrue(validationStateManager.expiryDateValidationState.notificationSent)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.expiryDateValidationState).willReturn(fieldValidationState)

        given(validationStateManager.expiryDateValidationState.validationState).willReturn(false)

        val validationResultHandler = ExpiryDateValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )
        validationResultHandler.handleResult(true)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should notify listener if notification previously sent when lifecycle is started`() {
        validationStateManager.expiryDateValidationState.notificationSent = true

        validationResultHandler.onStart()

        verify(validationListener).onExpiryDateValidated(false)
        assertTrue(validationStateManager.expiryDateValidationState.notificationSent)
    }

    @Test
    fun `should not notify listener if notification not previously sent when lifecycle is started`() {
        validationResultHandler.onStart()

        verifyZeroInteractions(validationListener)
    }

    @Test
    fun `should not notify listener on focus change if in life cycle event`() {
        validationResultHandler.onPause()

        validationResultHandler.handleFocusChange()
        verifyZeroInteractions(validationListener)

        validationResultHandler.onResume()
        validationResultHandler.handleFocusChange()
        verify(validationListener).onExpiryDateValidated(validationStateManager.expiryDateValidationState.validationState)
    }

}
