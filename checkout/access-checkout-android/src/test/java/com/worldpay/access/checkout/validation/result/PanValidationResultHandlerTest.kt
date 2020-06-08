package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PanValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutPanValidationListener>()
    private val validationStateManager = ValidationStateManager()

    private lateinit var validationResultHandler: PanValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when cvv is valid with brand`() {
        validationResultHandler.handleResult(true, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated.get())
    }

    @Test
    fun `should call listener when cvv is invalid with brand`() {
        validationResultHandler.handleResult(false, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call listener when cvv is valid with no brand`() {
        validationResultHandler.handleResult(true, null)

        verify(validationListener).onPanValidated(null, true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated.get())
    }

    @Test
    fun `should call listener when cvv is invalid with no brand`() {
        validationResultHandler.handleResult(false, null)

        verify(validationListener).onPanValidated(null, false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationStateManager = mock<ValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.panValidated).willReturn(AtomicBoolean(false))

        val validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(true, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}