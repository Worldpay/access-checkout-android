package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CVV_RULE
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.InputFilterHandler
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVVValidator
import org.junit.Before
import org.junit.Test

class CVVTextWatcherTest {

    private val inputFilterHandler = mock<InputFilterHandler>()
    private val cvvValidationResultHandler = mock<CvvValidationResultHandler>()

    private val panEditText = mock<EditText>()
    private val cvvEditText = mock<EditText>()

    private val cvvEditable = mock<Editable>()

    private lateinit var cvvTextWatcher: CVVTextWatcher

    @Before
    fun setup() {
        cvvTextWatcher = CVVTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panEditText = panEditText,
            cvvEditText = cvvEditText,
            cvvValidator = CVVValidator(),
            inputFilterHandler = inputFilterHandler,
            cvvValidationResultHandler = cvvValidationResultHandler
        )

        given(cvvEditable.toString()).willReturn("123")
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is identified`() {
        mockPan(VISA_PAN)

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(inputFilterHandler).handle(cvvEditText, VISA_BRAND.cvv)
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is not identified`() {
        mockPan("000000")

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(inputFilterHandler).handle(cvvEditText, CVV_RULE)
    }

    @Test
    fun `should pass validation result to validation result handler where brand is identified`() {
        mockPan(VISA_PAN)

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(cvvValidationResultHandler).handleResult(ValidationResult(partial = false, complete = true))
    }

    @Test
    fun `should pass validation result to validation result handler where brand is not identified`() {
        mockPan("000000")

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(cvvValidationResultHandler).handleResult(ValidationResult(partial = true, complete = true))
    }

    @Test
    fun `should pass empty pan value when pan edit text is not provided`() {
        cvvTextWatcher = CVVTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panEditText = null,
            cvvEditText = cvvEditText,
            cvvValidator = CVVValidator(),
            inputFilterHandler = inputFilterHandler,
            cvvValidationResultHandler = cvvValidationResultHandler
        )

        given(cvvEditable.toString()).willReturn("123")

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(cvvValidationResultHandler).handleResult(ValidationResult(partial = true, complete = true))
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val cvvValidator = mock<CVVValidator>()

        val cvvTextWatcher = CVVTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panEditText = panEditText,
            cvvEditText = cvvEditText,
            cvvValidator = cvvValidator,
            inputFilterHandler = inputFilterHandler,
            cvvValidationResultHandler = cvvValidationResultHandler
        )

        cvvTextWatcher.beforeTextChanged("", 1, 2,3)
        cvvTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            cvvValidator,
            cvvValidationResultHandler,
            inputFilterHandler
        )
    }

    private fun mockPan(value: String) {
        val panEditable = mock<Editable>()
        given(panEditText.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn(value)
    }

}
