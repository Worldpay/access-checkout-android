package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpiryDateValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutExpiryDateValidationListener>()
    private val validationStateManager =
        CardValidationStateManager()

    private lateinit var validationResultHandler: ExpiryDateValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = ExpiryDateValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when date is valid`() {
        validationResultHandler.handleResult(true)

        verify(validationListener).onExpiryDateValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.expiryDateValidated)
    }

    @Test
    fun `should call listener when date is invalid`() {
        validationResultHandler.handleResult(false)

        verify(validationListener).onExpiryDateValidated(false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.expiryDateValidated)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.expiryDateValidated).willReturn(false)

        val validationResultHandler = ExpiryDateValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(true)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
