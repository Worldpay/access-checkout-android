package com.worldpay.access.checkout.validation.listeners.focus

import android.view.View
import android.widget.EditText
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import com.worldpay.access.checkout.validation.result.handler.ExpiryDateValidationResultHandler
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class ExpiryDateFocusChangeListenerTest {

    private var mockParent = mock<AccessCheckoutEditText>()
    private var externalListener = mock<View.OnFocusChangeListener>()
    private val validationResultHandler = mock<ExpiryDateValidationResultHandler>()
    private lateinit var focusChangeListener: ExpiryDateFocusChangeListener

    @Before
    fun setup() {
        focusChangeListener =
            ExpiryDateFocusChangeListener(validationResultHandler)
    }

    @Test
    fun `should handle result when focus is lost`() {
        focusChangeListener.onFocusChange(null, false)

        verify(validationResultHandler).handleFocusChange() //Triggered validation
    }

    @Test
    fun `should do nothing when focus is gained`() {
        focusChangeListener.onFocusChange(null, true)

        verifyNoInteractions(validationResultHandler) //Do not trigger validation
    }

    @Test
    fun `should notify externalFocusChangeListener on parent when focus is gained`() {
        val view = simulateViewWithParent(withExternalListener = true)
        focusChangeListener.onFocusChange(view, true)

        verifyNoInteractions(validationResultHandler) //Do not trigger validation
        verify(externalListener).onFocusChange(mockParent, true)
    }

    @Test
    fun `should notify externalFocusChangeListener on parent when focus is lost`() {
        val view = simulateViewWithParent(withExternalListener = true)
        focusChangeListener.onFocusChange(view, false)

        verify(validationResultHandler).handleFocusChange() //Triggered validation
        verify(externalListener).onFocusChange(mockParent, false)
    }

    @Test
    fun `should not raise an exception if externalFocusChangeListener on parent is not set`() {
        val view = simulateViewWithParent(withExternalListener = false)
        focusChangeListener.onFocusChange(view, false)

        verify(validationResultHandler).handleFocusChange() //Triggered validation
        verifyNoInteractions(externalListener)
    }

    @Test
    fun `should not raise an exception if view is null`() {
        focusChangeListener.onFocusChange(null, false)

        verify(validationResultHandler).handleFocusChange() //Triggered validation
        verifyNoInteractions(externalListener)
    }

    private fun simulateViewWithParent(withExternalListener: Boolean = true): View {
        val child = mock<EditText>()
        `when`(child.parent).thenReturn(mockParent)
        if (withExternalListener) {
            `when`(mockParent.onFocusChangeListener).thenReturn(externalListener)
        } else {
            `when`(mockParent.onFocusChangeListener).thenReturn(null)
        }
        return child
    }
}
