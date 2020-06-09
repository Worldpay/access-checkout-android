package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewPANValidator
import org.junit.Before
import org.junit.Test

class PANTextWatcherTest {

    private val panValidationResultHandler = mock<PanValidationResultHandler>()

    private val cvvEditText = mock<EditText>()
    private val cvcValidationHandler = mock<CVCValidationHandler>()

    private val panValidator = mock<NewPANValidator>()
    private val panEditable = mock<Editable>()
    private val cvvEditable = mock<Editable>()

    private lateinit var panTextWatcher: PANTextWatcher

    @Before
    fun setup() {
        panTextWatcher = PANTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panValidator = NewPANValidator(),
            panValidationResultHandler = panValidationResultHandler,
            cvvEditText = cvvEditText,
            cvcValidationHandler = cvcValidationHandler
        )
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is identified`() {
        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(true, VISA_BRAND)
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is not identified`() {
        given(panEditable.toString()).willReturn("000000")

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(false, null)
    }

    @Test
    fun `should pass validation result to validation result handler where brand is identified`() {
        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(true, VISA_BRAND)
    }

    @Test
    fun `should pass validation result to validation result handler where brand is not identified`() {
        given(panEditable.toString()).willReturn("000000")

        panTextWatcher.afterTextChanged(panEditable)

        verify(panValidationResultHandler).handleResult(false, null)
    }

    @Test
    fun `should revalidate the cvv when the brand changes`() {
        given(panEditable.toString()).willReturn(VISA_PAN)
        given(cvvEditText.text).willReturn(cvvEditable)

        panTextWatcher.afterTextChanged(panEditable)

        verify(cvcValidationHandler).updateCvcRuleAndValidate(cvvEditable.toString(), VISA_BRAND.cvv)
    }

    @Test
    fun `should not revalidate the cvv when the brand doesn't change`() {
        given(panEditable.toString()).willReturn("000000")
        given(cvvEditText.text).willReturn(cvvEditable)

        panTextWatcher.afterTextChanged(panEditable)

        verify(cvcValidationHandler, times(0)).updateCvcRuleAndValidate(any(), any())
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val panValidator = mock<NewPANValidator>()

        val panTextWatcher = PANTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panValidator = panValidator,
            panValidationResultHandler = panValidationResultHandler,
            cvvEditText = cvvEditText,
            cvcValidationHandler = cvcValidationHandler
        )

        panTextWatcher.beforeTextChanged("", 1, 2,3)
        panTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            panValidator,
            panValidationResultHandler
        )
    }

}
