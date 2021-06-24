package com.worldpay.access.checkout.validation.listeners.focus

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.validation.result.handler.ExpiryDateValidationResultHandler
import org.junit.Before
import org.junit.Test

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

        verifyZeroInteractions(expiryDateValidationResultHandler)
    }
}
