package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.validation.result.state.PanFieldValidationStateManager
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class PanValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutPanValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()
    private val validationStateManager = mock<PanFieldValidationStateManager>()

    private lateinit var validationResultHandler: PanValidationResultHandler

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        validationResultHandler = PanValidationResultHandler(
            validationListener,
            validationStateManager,
            lifecycleOwner
        )
    }

    @Test
    fun `should notify listener with true when isValid is true`() {
        given(validationStateManager.isAllValid()).willReturn(false)

        validationResultHandler.notifyListener(true)

        verify(validationListener).onPanValidated(true)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should notify listener with false when isValid is false`() {
        given(validationStateManager.isAllValid()).willReturn(false)

        validationResultHandler.notifyListener(false)

        verify(validationListener).onPanValidated(false)
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        given(validationStateManager.isAllValid()).willReturn(true)

        validationResultHandler.notifyListener(true)

        verify(validationListener).onPanValidated(true)
        verify(validationStateManager).isAllValid()
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should not call onValidationSuccess when all fields are not valid`() {
        given(validationStateManager.isAllValid()).willReturn(false)

        validationResultHandler.notifyListener(true)

        verify(validationListener).onPanValidated(true)
        verify(validationStateManager).isAllValid()
        verifyNoMoreInteractions(validationListener)
    }

    @Test
    fun `should not bother checking if all fields are valid is isValid is false`() {
        validationResultHandler.notifyListener(false)

        verifyNoInteractions(validationStateManager)
    }
}
