package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
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
            cvvEditText = cvvEditText,
            cvcValidator = cvcValidator,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        given(cvvEditText.text).willReturn(cvvEditable)
        given(cvvEditable.toString()).willReturn("")
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is identified`() {
        val cardBrand = toCardBrandTransformer.transform(VISA_BRAND)

        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(true, cardBrand)
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is not identified`() {
        given(panEditable.toString()).willReturn("000000")

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(false, null)
    }

    @Test
    fun `should pass validation result to validation result handler where brand is identified`() {
        val cardBrand = toCardBrandTransformer.transform(VISA_BRAND)

        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(true, cardBrand)
    }

    @Test
    fun `should pass validation result to validation result handler where brand is not identified`() {
        given(panEditable.toString()).willReturn("000000")

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(false, null)
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
