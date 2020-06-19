package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PANTextWatcherTest {

    private lateinit var cvcValidationRuleManager: CVCValidationRuleManager

    private val toCardBrandTransformer = ToCardBrandTransformer()

    private val panValidationResultHandler = mock<PanValidationResultHandler>()
    private val brandChangedHandler = mock<BrandChangedHandler>()

    private val cvvEditText = mock<EditText>()
    private val cvcValidator = mock<CVCValidator>()

    private val panEditable = mock<Editable>()
    private val cvvEditable = mock<Editable>()

    private lateinit var panTextWatcher: PANTextWatcher

    @Before
    fun setup() {
        cvcValidationRuleManager = CVCValidationRuleManager()

        panTextWatcher = PANTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panValidator = NewPANValidator(),
            panValidationResultHandler = panValidationResultHandler,
            brandChangedHandler = brandChangedHandler,
            cvvEditText = cvvEditText,
            cvcValidator = cvcValidator,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        given(cvvEditText.text).willReturn(cvvEditable)
        given(cvvEditable.toString()).willReturn("")
    }

    @Test
    fun `should pass validation rule to validation rule handler where result is true for known brand`() {
        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should pass validation rule to validation rule handler where result is true for unknown brand`() {
        given(panEditable.toString()).willReturn(VALID_UNKNOWN_LUHN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should pass validation rule to validation rule handler where result is false for unknown brand`() {
        given(panEditable.toString()).willReturn("000000")

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should pass validation rule to validation rule handler where result is false for known brand`() {
        given(panEditable.toString()).willReturn("411")

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should revalidate the cvv when the brand changes and cvv is not empty`() {
        given(panEditable.toString()).willReturn(VISA_PAN)
        given(cvvEditable.toString()).willReturn("123")

        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())

        panTextWatcher.afterTextChanged(panEditable)

        assertEquals(VISA_BRAND.cvv, cvcValidationRuleManager.getRule())

        verify(cvcValidator).validate("123")
    }

    @Test
    fun `should handle brand change when brand is different from the last known brand`() {
        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(brandChangedHandler).handle(VISA_BRAND)
    }

    @Test
    fun `should not revalidate the cvv if the cvv is blank and should only update the rule`() {
        given(panEditable.toString()).willReturn(VISA_PAN)
        given(cvvEditable.toString()).willReturn("")

        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())

        panTextWatcher.afterTextChanged(panEditable)

        assertEquals(VISA_BRAND.cvv, cvcValidationRuleManager.getRule())
        verify(cvcValidator, never()).validate(any())
    }

    @Test
    fun `should not revalidate the cvv when the brand doesn't change`() {
        given(panEditable.toString()).willReturn("000000")

        panTextWatcher.afterTextChanged(panEditable)

        verify(cvcValidator, never()).validate(any())
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val panValidator = mock<NewPANValidator>()

        val panTextWatcher = PANTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panValidator = panValidator,
            panValidationResultHandler = panValidationResultHandler,
            brandChangedHandler = brandChangedHandler,
            cvvEditText = cvvEditText,
            cvcValidator = cvcValidator,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        panTextWatcher.beforeTextChanged("", 1, 2,3)
        panTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            panValidator,
            panValidationResultHandler
        )
    }

}
