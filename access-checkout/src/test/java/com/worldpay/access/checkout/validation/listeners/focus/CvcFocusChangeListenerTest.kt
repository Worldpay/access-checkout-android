package com.worldpay.access.checkout.validation.listeners.focus

import com.worldpay.access.checkout.validation.result.handler.CvcValidationResultHandler
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class CvcFocusChangeListenerTest {

    private val cvcValidationResultHandler = mock<CvcValidationResultHandler>()

    private lateinit var cvcFocusChangeListener: CvcFocusChangeListener

    @Before
    fun setup() {
        cvcFocusChangeListener = CvcFocusChangeListener(cvcValidationResultHandler)
    }

    @Test
    fun `should handle result when focus is lost`() {
        cvcFocusChangeListener.onFocusChange(null, false)

        verify(cvcValidationResultHandler).handleFocusChange()
    }

    @Test
    fun `should do nothing when focus is gained`() {
        cvcFocusChangeListener.onFocusChange(null, true)

        verifyNoInteractions(cvcValidationResultHandler)
    }
}
