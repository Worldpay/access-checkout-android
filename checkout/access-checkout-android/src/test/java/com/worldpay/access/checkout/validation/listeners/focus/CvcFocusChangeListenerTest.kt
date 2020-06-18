package com.worldpay.access.checkout.validation.listeners.focus

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import org.junit.Before
import org.junit.Test

class CvcFocusChangeListenerTest {

    private val cvvValidationResultHandler = mock<CvvValidationResultHandler>()

    private lateinit var cvcFocusChangeListener : CvcFocusChangeListener

    @Before
    fun setup() {
        cvcFocusChangeListener = CvcFocusChangeListener(cvvValidationResultHandler)
    }

    @Test
    fun `should handle result when focus is lost`() {
        cvcFocusChangeListener.onFocusChange(null, false)

        verify(cvvValidationResultHandler).handleFocusChange()
    }

    @Test
    fun `should do nothing when focus is gained`() {
        cvcFocusChangeListener.onFocusChange(null, true)

        verifyZeroInteractions(cvvValidationResultHandler)
    }

}
