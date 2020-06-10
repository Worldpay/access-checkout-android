package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.AccessCheckoutExpiryDateValidationListener
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpiryDateValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutExpiryDateValidationListener>()
    private val validationStateManager = ValidationStateManager()

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

        assertTrue(validationStateManager.expiryDateValidated.get())
    }

    @Test
    fun `should call listener when date is invalid`() {
        validationResultHandler.handleResult(false)

        verify(validationListener).onExpiryDateValidated(false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.expiryDateValidated.get())
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<ValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.expiryDateValidated).willReturn(AtomicBoolean(false))

        val validationResultHandler = ExpiryDateValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(true)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
