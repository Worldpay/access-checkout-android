package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.PanValidator
import org.junit.Before
import org.junit.Test

class PanTextWatcherTest {

    private val cvcValidationRuleManager = mock<CVCValidationRuleManager>()
    private val panValidator = mock<PanValidator>()
    private val panValidationResultHandler = mock<PanValidationResultHandler>()
    private val brandChangedHandler = mock<BrandChangedHandler>()

    private val cvcEditText = mock<EditText>()
    private val cvcValidator = mock<CvcValidator>()

    private val panEditable = mock<Editable>()
    private val cvcEditable = mock<Editable>()

    private lateinit var panTextWatcher: PanTextWatcher

    @Before
    fun setup() {
        mockSuccessfulCardConfiguration()

        panTextWatcher = PanTextWatcher(
            panValidator = panValidator,
            panValidationResultHandler = panValidationResultHandler,
            brandChangedHandler = brandChangedHandler,
            cvcEditText = cvcEditText,
            cvcValidator = cvcValidator,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        given(cvcEditText.text).willReturn(cvcEditable)
        given(cvcEditable.toString()).willReturn("")
    }

    @Test
    fun `should call the pan validator with the correct validation rule`() {
        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidator).validate(VISA_PAN, VISA_BRAND.pan, VISA_BRAND)
    }

    @Test
    fun `should call the pan result handler with valid result when the pan validator returns true`() {
        mockPan(VISA_PAN, true)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should call the pan result handler with invalid result when the pan validator returns false`() {
        mockPan(INVALID_UNKNOWN_LUHN, false)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should call the brand changed handler with visa brand regardless of the pan validation result`() {
        mockPan(VISA_PAN, true)

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        mockPan(INVALID_UNKNOWN_LUHN, false)

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(null)
    }

    @Test
    fun `should not call the brand changed handler when unknown pan is entered for the first time`() {
        mockPan(VALID_UNKNOWN_LUHN, true)

        panTextWatcher.afterTextChanged(panEditable)

        verifyZeroInteractions(brandChangedHandler)
    }

    @Test
    fun `should not call the brand changed handler if the brand has not actually changed from the previous one`() {
        // set the visa pan so that the brand changed handler is called with visa
        mockPan(VISA_PAN, true)
        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        reset(brandChangedHandler)

        // set the visa pan again so that the brand changed handler is no longer called
        mockPan(VISA_PAN, true)
        panTextWatcher.afterTextChanged(panEditable)
        verifyZeroInteractions(brandChangedHandler)
    }

    @Test
    fun `should update the cvc validation rule when the brand changes`() {
        mockPan(VISA_PAN, true)

        panTextWatcher.afterTextChanged(panEditable)

        verify(brandChangedHandler).handle(VISA_BRAND)
        verify(cvcValidationRuleManager).updateRule(VISA_BRAND.cvc)
    }

    @Test
    fun `should re-validate the cvc when the brand changes`() {
        mockPan(VISA_PAN, true)
        given(cvcEditable.toString()).willReturn("123")

        panTextWatcher.afterTextChanged(panEditable)

        verify(brandChangedHandler).handle(VISA_BRAND)
        verify(cvcValidator).validate("123")
    }

    @Test
    fun `should not interact with the cvc validator at all if the brand has not changed`() {
        // set the visa pan so that the brand changed handler is called with visa
        mockPan(VISA_PAN, true)
        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        reset(brandChangedHandler)

        // set the visa pan again so that the brand changed handler is no longer called
        mockPan(VISA_PAN, true)
        panTextWatcher.afterTextChanged(panEditable)
        verifyZeroInteractions(brandChangedHandler)
        verifyZeroInteractions(cvcValidator)
    }

    @Test
    fun `should not interact with the cvc validator at all if the cvc is empty`() {
        mockPan(VISA_PAN, true)
        given(cvcEditable.toString()).willReturn("")

        panTextWatcher.afterTextChanged(panEditable)

        verify(brandChangedHandler).handle(VISA_BRAND)
        verifyZeroInteractions(cvcValidator)
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val panValidator = mock<PanValidator>()

        val panTextWatcher = PanTextWatcher(
            panValidator = panValidator,
            panValidationResultHandler = panValidationResultHandler,
            brandChangedHandler = brandChangedHandler,
            cvcEditText = cvcEditText,
            cvcValidator = cvcValidator,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        panTextWatcher.beforeTextChanged("", 1, 2, 3)
        panTextWatcher.onTextChanged("", 1, 2, 3)

        verifyZeroInteractions(
            panValidator,
            panValidationResultHandler
        )
    }

    private fun mockPan(pan: String, isValid: Boolean) {
        given(panEditable.toString()).willReturn(pan)
        given(panValidator.validate(eq(pan), any(), any())).willReturn(isValid)
    }

}
