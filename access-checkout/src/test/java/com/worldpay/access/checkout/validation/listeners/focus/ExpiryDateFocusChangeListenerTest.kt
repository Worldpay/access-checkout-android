package com.worldpay.access.checkout.validation.listeners.focus

import com.worldpay.access.checkout.validation.result.handler.ExpiryDateValidationResultHandler
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class ExpiryDateFocusChangeListenerTest {

    private val expiryDateValidationResultHandler = mock<ExpiryDateValidationResultHandler>()

    private lateinit var expiryDateFocusChangeListener: ExpiryDateFocusChangeListener

    @Before
    fun setup() {
        expiryDateFocusChangeListener = ExpiryDateFocusChangeListener(expiryDateValidationResultHandler)
    }

    @Test
    fun `should handle result when focus is lost`() {
        expiryDateFocusChangeListener.onFocusChange(null, false)

        verify(expiryDateValidationResultHandler).handleFocusChange()
    }

    @Test
    fun `should do nothing when focus is gained`() {
        expiryDateFocusChangeListener.onFocusChange(null, true)

        verifyNoInteractions(expiryDateValidationResultHandler)
    }
}
