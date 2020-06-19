package com.worldpay.access.checkout.validation.result.handler

import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PanValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutPanValidationListener>()
    private val validationStateManager = CardValidationStateManager()

    private lateinit var validationResultHandler: PanValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = PanValidationResultHandler(
            validationListener,
            validationStateManager
        )
    }

    @Test
    fun `should call listener when pan is valid and was previously invalid`() {
        validationResultHandler.handleResult(false)

        validationResultHandler.handleResult(true)

        verify(validationListener).onPanValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidationState)
    }

    @Test
    fun `should call listener when pan is invalid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verify(validationListener).onPanValidated( false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.panValidationState)
    }

    @Test
    fun `should not call listener when pan is valid and was previously valid`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleResult(true)

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.panValidationState)
    }

    @Test
    fun `should not call listener when pan is invalid and was previously invalid`() {
        validationResultHandler.handleResult(false)
        reset(validationListener)

        validationResultHandler.handleResult(false)

        verifyZeroInteractions(validationListener)

        assertFalse(validationStateManager.panValidationState)
    }

    @Test
    fun `should call listener when focus is changed and notification has not been sent previously`() {
        validationResultHandler.handleFocusChange()

        verify(validationListener).onPanValidated(false)

        assertFalse(validationStateManager.panValidationState)
    }

    @Test
    fun `should not call listener when focus is changed and notification has been sent previously`() {
        validationResultHandler.handleResult(true)
        reset(validationListener)

        validationResultHandler.handleFocusChange()

        verifyZeroInteractions(validationListener)

        assertTrue(validationStateManager.panValidationState)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.panValidationState).willReturn(false)

        val validationResultHandler = PanValidationResultHandler(
            validationListener,
            validationStateManager
        )
        validationResultHandler.handleResult(true)

        verify(validationListener).onPanValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
