package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.InputFilter
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewPANValidator
import org.junit.Before
import org.junit.Test

class PANTextWatcherTest {

    private val inputFilter = mock<InputFilter>()
    private val panValidationResultHandler = mock<PanValidationResultHandler>()

    private val panEditText = mock<EditText>()
    private val panEditable = mock<Editable>()

    private lateinit var panTextWatcher: PANTextWatcher

    @Before
    fun setup() {
        panTextWatcher = PANTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panValidator = NewPANValidator(),
            inputFilter = inputFilter,
            panValidationResultHandler = panValidationResultHandler,
            panEditText = panEditText
        )
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is identified`() {
        given(panEditable.toString()).willReturn(VISA_PAN)

        panTextWatcher.afterTextChanged(panEditable)

        verify(inputFilter).filter(panEditText, VISA_BRAND.pan)
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is not identified`() {
        given(panEditable.toString()).willReturn("000000")

        panTextWatcher.afterTextChanged(panEditable)

        verify(inputFilter).filter(panEditText, PAN_RULE)
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
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val panValidator = mock<NewPANValidator>()

        val panTextWatcher = PANTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panValidator = panValidator,
            inputFilter = inputFilter,
            panValidationResultHandler = panValidationResultHandler,
            panEditText = panEditText
        )

        panTextWatcher.beforeTextChanged("", 1, 2,3)
        panTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            panValidator,
            panValidationResultHandler,
            inputFilter
        )
    }

}
