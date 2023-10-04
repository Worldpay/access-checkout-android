package com.worldpay.access.checkout.validation.listeners.focus

import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class PanFocusChangeListenerTest {

    private val panValidationResultHandler = mock<PanValidationResultHandler>()

    private lateinit var panFocusChangeListener: PanFocusChangeListener

    @Before
    fun setup() {
        panFocusChangeListener = PanFocusChangeListener(panValidationResultHandler)
    }

    @Test
    fun `should handle result when focus is lost`() {
        panFocusChangeListener.onFocusChange(null, false)

        verify(panValidationResultHandler).handleFocusChange()
    }

    @Test
    fun `should do nothing when focus is gained`() {
        panFocusChangeListener.onFocusChange(null, true)

        verifyNoInteractions(panValidationResultHandler)
    }
}
