package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PanValidationResultHandlerTest {

    private val toCardBrandTransformer = ToCardBrandTransformer()

    private val validationListener = mock<AccessCheckoutPanValidationListener>()
    private val validationStateManager =
        CardValidationStateManager()

    private lateinit var validationResultHandler: PanValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when cvv is valid with brand`() {
        val cardBrand = toCardBrandTransformer.transform(VISA_BRAND)

        validationResultHandler.handleResult(true, cardBrand)

        verify(validationListener).onPanValidated(cardBrand, true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated)
    }

    @Test
    fun `should call listener when cvv is invalid with brand`() {
        val cardBrand = toCardBrandTransformer.transform(VISA_BRAND)

        validationResultHandler.handleResult(false, cardBrand)

        verify(validationListener).onPanValidated(cardBrand, false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated)
    }

    @Test
    fun `should call listener when cvv is valid with no brand`() {
        validationResultHandler.handleResult(true, null)

        verify(validationListener).onPanValidated(null, true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.panValidated)
    }

    @Test
    fun `should call listener when cvv is invalid with no brand`() {
        validationResultHandler.handleResult(false, null)

        verify(validationListener).onPanValidated(null, false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated)
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val cardBrand = toCardBrandTransformer.transform(VISA_BRAND)
        val validationStateManager = mock<CardValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.panValidated).willReturn(false)

        val validationResultHandler = PanValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(true, cardBrand)

        verify(validationListener).onPanValidated(cardBrand, true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}
