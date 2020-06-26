package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.result.state.FieldValidationState
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvcValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutCvcValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()
    private val validationStateManager = CardValidationStateManager

    private lateinit var validationResultHandler: CvcValidationResultHandler

    @Before
    fun setup() {
        BDDMockito.given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        validationStateManager.cvcValidationState.validationState = false
        validationStateManager.cvcValidationState.notificationSent = false
        reset(validationListener)

        validationResultHandler = CvcValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )

    }

    @Test
    fun `should call listener when cvc is valid and was previously invalid and set notification sent state`() {
        validationResultHandler.handleResult(true)

        verify(validationListener).onCvcValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.cvcValidationState.validationState)
        assertTrue(validationStateManager.cvcValidationState.notificationSent)
    }

    @Test
    fun `should call listener when cvc is invalid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verify(validationListener).onCvcValidated( false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvcValidationState.validationState)
        assertTrue(validationStateManager.cvcValidationState.notificationSent)
    }

    @Test
    fun `should not call listener when cvc is valid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(true)

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.cvcValidationState.validationState)
    }

    @Test
    fun `should not call listener when cvc is invalid and was previously invalid`() {
        validationResultHandler.handleResult(false)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verifyZeroInteractions(validationListener)

        assertFalse(validationStateManager.cvcValidationState.validationState)
    }

    @Test
    fun `should call listener when focus is changed and notification has not been sent previously`() {
        validationResultHandler.handleFocusChange()

        verify(validationListener).onCvcValidated(false)

        assertFalse(validationStateManager.cvcValidationState.validationState)
        assertTrue(validationStateManager.cvcValidationState.notificationSent)
    }

    @Test
    fun `should not call listener when focus is changed and notification has been sent previously`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleFocusChange()

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.cvcValidationState.validationState)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationResult = true
        val fieldValidationState = mock<FieldValidationState>()

        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.cvcValidationState).willReturn(fieldValidationState)
        given(validationStateManager.cvcValidationState.validationState).willReturn(false)

        val validationResultHandler = CvcValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )

        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvcValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should notify listener if notification previously sent when lifecycle is started`() {
        validationStateManager.cvcValidationState.notificationSent = true

        validationResultHandler.onStart()

        verify(validationListener).onCvcValidated(false)
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
        verify(validationListener).onCvcValidated(validationStateManager.cvcValidationState.validationState)
    }



}
