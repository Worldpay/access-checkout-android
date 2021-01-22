package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.validation.result.state.ExpiryDateFieldValidationStateManager
import org.junit.Before
import org.junit.Test

class ExpiryDateValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutExpiryDateValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()
    private val validationStateManager = mock<ExpiryDateFieldValidationStateManager>()

    private lateinit var validationResultHandler: ExpiryDateValidationResultHandler

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        validationResultHandler = ExpiryDateValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )
    }

    @Test
    fun `should notify listener with true when isValid is true`() {
        given(validationStateManager.isAllValid()).willReturn(false)

        validationResultHandler.notifyListener(true)

        verify(validationListener).onExpiryDateValidated(true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should notify listener with false when isValid is false`() {
        given(validationStateManager.isAllValid()).willReturn(false)

        validationResultHandler.notifyListener(false)

        verify(validationListener).onExpiryDateValidated(false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        given(validationStateManager.isAllValid()).willReturn(true)

        validationResultHandler.notifyListener(true)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationStateManager).isAllValid()
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should not call onValidationSuccess when all fields are not valid`() {
        given(validationStateManager.isAllValid()).willReturn(false)

        validationResultHandler.notifyListener(true)

        verify(validationListener).onExpiryDateValidated(true)
        verify(validationStateManager).isAllValid()
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should not bother checking if all fields are valid is isValid is false`() {
        validationResultHandler.notifyListener(false)

        verifyZeroInteractions(validationStateManager)
    }
}
