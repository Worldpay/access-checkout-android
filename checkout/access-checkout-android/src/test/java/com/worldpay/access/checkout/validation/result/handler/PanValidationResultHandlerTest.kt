package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.result.state.FieldValidationState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PanValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutPanValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private val validationStateManager = CardValidationStateManager
    private val fieldValidationState = mock<FieldValidationState>()

    private lateinit var validationResultHandler: PanValidationResultHandler

    @Before
    fun setup() {
        validationStateManager.panValidationState.validationState = false
        validationStateManager.panValidationState.notificationSent = false

        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        validationResultHandler = PanValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )
    }

    @Test
    fun `should call listener when pan is valid and was previously invalid`() {
        validationResultHandler.handleResult(false)

        validationResultHandler.handleResult(true)

        verify(validationListener).onPanValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidationState.validationState)
    }

    @Test
    fun `should call listener when pan is invalid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verify(validationListener).onPanValidated( false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.panValidationState.validationState)
    }

    @Test
    fun `should not call listener when pan is valid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(true)

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.panValidationState.validationState)
    }

    @Test
    fun `should not call listener when pan is invalid and was previously invalid`() {
        validationResultHandler.handleResult(false)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verifyZeroInteractions(validationListener)

        assertFalse(validationStateManager.panValidationState.validationState)
    }

    @Test
    fun `should call listener when focus is changed and notification has not been sent previously`() {
        validationResultHandler.handleFocusChange()

        verify(validationListener).onPanValidated(false)

        assertFalse(validationStateManager.panValidationState.validationState)
    }

    @Test
    fun `should not call listener when focus is changed and notification has been sent previously`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleFocusChange()

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.panValidationState.validationState)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.panValidationState).willReturn(fieldValidationState)
        given(validationStateManager.panValidationState.validationState).willReturn(false)

        val validationResultHandler = PanValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )

        validationResultHandler.handleResult(true)

        verify(validationListener).onPanValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}