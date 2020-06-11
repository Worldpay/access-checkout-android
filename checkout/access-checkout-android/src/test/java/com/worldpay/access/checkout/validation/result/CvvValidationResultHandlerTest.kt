package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvvValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutCvvValidationListener>()
    private val validationStateManager =
        CardValidationStateManager()

    private lateinit var validationResultHandler: CvvValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = CvvValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when cvv is valid`() {
        val validationResult = true

        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvvValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call listener when cvv is invalid`() {
        val validationResult = false

        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvvValidated(false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationResult = true

        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.cvvValidated).willReturn(AtomicBoolean(false))

        val validationResultHandler = CvvValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvvValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
