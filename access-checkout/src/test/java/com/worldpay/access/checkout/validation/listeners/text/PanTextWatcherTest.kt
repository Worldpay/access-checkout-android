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
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN_FORMATTED
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import com.worldpay.access.checkout.validation.formatter.PanFormatter
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.PanValidator
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.CARD_BRAND_NOT_ACCEPTED
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.INVALID
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.INVALID_LUHN
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.VALID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PanTextWatcherTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val cvcValidationRuleManager = mock<CVCValidationRuleManager>()
    private val panValidator = mock<PanValidator>()
    private val panFormatter = mock<PanFormatter>()
    private val panValidationResultHandler = mock<PanValidationResultHandler>()
    private val brandChangedHandler = mock<BrandChangedHandler>()

    private val panEditText = mock<EditText>()

    private val cvcEditText = mock<EditText>()
    private val cvcValidator = mock<CvcValidator>()

    private val panEditable = mock<Editable>()
    private val cvcEditable = mock<Editable>()

    private lateinit var panTextWatcher: PanTextWatcher

    @Before
    fun setup() = runAsBlockingTest {
        mockSuccessfulCardConfiguration()

        panTextWatcher = PanTextWatcher(
            panEditText = panEditText,
            panValidator = panValidator,
            panFormatter = panFormatter,
            cvcValidator = cvcValidator,
            cvcEditText = cvcEditText,
            panValidationResultHandler = panValidationResultHandler,
            brandChangedHandler = brandChangedHandler,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        given(cvcEditText.text).willReturn(cvcEditable)
        given(cvcEditable.toString()).willReturn("")
        given(panFormatter.format(visaPan(), VISA_BRAND)).willReturn(visaPan())
    }

    @Test
    fun `should call the pan validator with the correct validation rule and brand`() {
        given(panEditable.toString()).willReturn(visaPan())

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidator).validate(visaPan(), VISA_BRAND.pan, VISA_BRAND)
    }

    @Test
    fun `should call the pan result handler with valid result and not force notify when the pan validator returns VALID`() {
        mockPan(visaPan(), VALID)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(isValid = true, forceNotify = false)
    }

    @Test
    fun `should call the pan result handler with invalid result and not force notify when the pan validator returns INVALID`() {
        mockPan("", INVALID)
        given(panFormatter.format("", null)).willReturn("")

        panTextWatcher.afterTextChanged(panEditable)

        verifyZeroInteractions(panValidationResultHandler)
    }

    @Test
    fun `should call the pan result handler with invalid result and force notify when the pan validator returns CARD_BRAND_NOT_ACCEPTED`() {
        mockPan(visaPan(), CARD_BRAND_NOT_ACCEPTED)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(isValid = false, forceNotify = true)
    }

    @Test
    fun `should call the pan result handler with invalid result and not force notify when the pan validator returns INVALID_LUHN`() {
        mockPan(INVALID_UNKNOWN_LUHN, INVALID_LUHN)
        given(panFormatter.format(INVALID_UNKNOWN_LUHN, null)).willReturn(INVALID_UNKNOWN_LUHN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(isValid = false, forceNotify = false)
    }

    @Test
    fun `should call the brand changed handler with visa brand regardless of the pan validation result being INVALID`() {
        mockPan(visaPan(), VALID)
        given(panFormatter.format(visaPan(), VISA_BRAND)).willReturn(visaPan())

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        mockPan(INVALID_UNKNOWN_LUHN, INVALID)
        given(panFormatter.format(INVALID_UNKNOWN_LUHN, null)).willReturn(INVALID_UNKNOWN_LUHN)

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(null)
    }

    @Test
    fun `should call the brand changed handler with visa brand regardless of the pan validation result being CARD_BRAND_NOT_ACCEPTED`() {
        mockPan(visaPan(), VALID)
        given(panFormatter.format(visaPan(), VISA_BRAND)).willReturn(visaPan())

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        mockPan(INVALID_UNKNOWN_LUHN, CARD_BRAND_NOT_ACCEPTED)
        given(panFormatter.format(INVALID_UNKNOWN_LUHN, null)).willReturn(INVALID_UNKNOWN_LUHN)

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(null)
    }

    @Test
    fun `should call the brand changed handler with visa brand regardless of the pan validation result being INVALID_LUHN`() {
        mockPan(visaPan(), VALID)
        given(panFormatter.format(visaPan(), VISA_BRAND)).willReturn(visaPan())

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        mockPan(INVALID_UNKNOWN_LUHN, INVALID_LUHN)
        given(panFormatter.format(INVALID_UNKNOWN_LUHN, null)).willReturn(INVALID_UNKNOWN_LUHN)

        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(null)
    }

    @Test
    fun `should not call the brand changed handler when unknown pan is entered for the first time`() {
        mockPan(VALID_UNKNOWN_LUHN, VALID)
        given(panFormatter.format(VALID_UNKNOWN_LUHN, null)).willReturn(VALID_UNKNOWN_LUHN_FORMATTED)

        panTextWatcher.afterTextChanged(panEditable)

        verifyZeroInteractions(brandChangedHandler)
    }

    @Test
    fun `should not call the brand changed handler if the brand has not actually changed from the previous one`() {
        // set the visa pan so that the brand changed handler is called with visa
        mockPan(visaPan(), VALID)
        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        reset(brandChangedHandler)

        // set the visa pan again so that the brand changed handler is no longer called
        mockPan(visaPan(), VALID)
        panTextWatcher.afterTextChanged(panEditable)
        verifyZeroInteractions(brandChangedHandler)
    }

    @Test
    fun `should update the cvc validation rule when the brand changes`() {
        mockPan(visaPan(), VALID)

        panTextWatcher.afterTextChanged(panEditable)

        verify(brandChangedHandler).handle(VISA_BRAND)
        verify(cvcValidationRuleManager).updateRule(VISA_BRAND.cvc)
    }

    @Test
    fun `should re-validate the cvc when the brand changes`() {
        mockPan(visaPan(), VALID)
        given(cvcEditable.toString()).willReturn("123")

        panTextWatcher.afterTextChanged(panEditable)

        verify(brandChangedHandler).handle(VISA_BRAND)
        verify(cvcValidator).validate("123")
    }

    @Test
    fun `should not interact with the cvc validator at all if the brand has not changed`() {
        // set the visa pan so that the brand changed handler is called with visa
        mockPan(visaPan(), VALID)
        panTextWatcher.afterTextChanged(panEditable)
        verify(brandChangedHandler).handle(VISA_BRAND)

        reset(brandChangedHandler)

        // set the visa pan again so that the brand changed handler is no longer called
        mockPan(visaPan(), VALID)
        panTextWatcher.afterTextChanged(panEditable)
        verifyZeroInteractions(brandChangedHandler)
        verifyZeroInteractions(cvcValidator)
    }

    @Test
    fun `should not interact with the cvc validator at all if the cvc is empty`() {
        mockPan(visaPan(), VALID)
        given(cvcEditable.toString()).willReturn("")

        panTextWatcher.afterTextChanged(panEditable)

        verify(brandChangedHandler).handle(VISA_BRAND)
        verifyZeroInteractions(cvcValidator)
    }

    private fun mockPan(pan: String, isValid: PanValidationResult) {
        given(panEditable.toString()).willReturn(pan)
        given(panValidator.validate(eq(pan), any(), any())).willReturn(isValid)
    }
}
