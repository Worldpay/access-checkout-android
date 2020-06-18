package com.worldpay.access.checkout.validation.listeners.focus

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import org.junit.Before
import org.junit.Test

class PanFocusChangeListenerTest {

    private val panValidationResultHandler = mock<PanValidationResultHandler>()

    private lateinit var panFocusChangeListener : PanFocusChangeListener

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

        verifyZeroInteractions(panValidationResultHandler)
    }

}
