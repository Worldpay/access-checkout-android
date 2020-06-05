package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.AccessCheckoutPanValidatedSuccessListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PanValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutPanValidatedSuccessListener>()
    private val validationStateManager = ValidationStateManager()

    private lateinit var validationResultHandler: PanValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when cvv is valid with brand`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        validationResultHandler.handleResult(validationResult, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated.get())
    }

    @Test
    fun `should call listener when cvv is invalid with brand`() {
        val validationResult = ValidationResult(partial = true, complete = false)

        validationResultHandler.handleResult(validationResult, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call listener when cvv is valid with no brand`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        validationResultHandler.handleResult(validationResult, null)

        verify(validationListener).onPanValidated(null, true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated.get())
    }

    @Test
    fun `should call listener when cvv is invalid with no brand`() {
        val validationResult = ValidationResult(partial = true, complete = false)

        validationResultHandler.handleResult(validationResult, null)

        verify(validationListener).onPanValidated(null, false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call onValidationSuccess when all fields are field`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        val validationStateManager = mock<ValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.panValidated).willReturn(AtomicBoolean(false))

        val validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(validationResult, VISA_BRAND)

        verify(validationListener).onPanValidated(VISA_BRAND, true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}