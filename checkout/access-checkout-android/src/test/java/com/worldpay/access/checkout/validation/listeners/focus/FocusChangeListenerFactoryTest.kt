package com.worldpay.access.checkout.validation.listeners.focus

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.validation.result.ResultHandlerFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class FocusChangeListenerFactoryTest {

    private val resultHandlerFactory = mock<ResultHandlerFactory>()

    private lateinit var focusChangeListenerFactory : FocusChangeListenerFactory

    @Before
    fun setup() {
        focusChangeListenerFactory = FocusChangeListenerFactory(resultHandlerFactory)
    }

    @Test
    fun `should get pan focus change listener`() {
        given(resultHandlerFactory.getPanValidationResultHandler()).willReturn(mock())
        assertNotNull(focusChangeListenerFactory.createPanFocusChangeListener())
    }

    @Test
    fun `should get cvc focus change listener`() {
        given(resultHandlerFactory.getCvvValidationResultHandler()).willReturn(mock())
        assertNotNull(focusChangeListenerFactory.createCvcFocusChangeListener())
    }

    @Test
    fun `should get expiry date focus change listener`() {
        given(resultHandlerFactory.getExpiryDateValidationResultHandler()).willReturn(mock())
        assertNotNull(focusChangeListenerFactory.createExpiryDateFocusChangeListener())
    }

}
