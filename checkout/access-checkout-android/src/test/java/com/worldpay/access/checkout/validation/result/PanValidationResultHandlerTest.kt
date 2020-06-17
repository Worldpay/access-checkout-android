package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
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
        validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when pan is valid with brand`() {
        validationResultHandler.handleResult(true)

        verify(validationListener).onPanValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated)
    }

    @Test
    fun `should call listener when pan is invalid with brand`() {
        validationResultHandler.handleResult(false)

        verify(validationListener).onPanValidated( false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.panValidated)
    }

    @Test
    fun `should call listener when pan is valid with no brand`() {
        validationResultHandler.handleResult(true)

        verify(validationListener).onPanValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated)
    }

    @Test
    fun `should call listener when pan is invalid with no brand`() {
        validationResultHandler.handleResult(false)

        verify(validationListener).onPanValidated(false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.panValidated)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.panValidated).willReturn(false)

        val validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(true)

        verify(validationListener).onPanValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
