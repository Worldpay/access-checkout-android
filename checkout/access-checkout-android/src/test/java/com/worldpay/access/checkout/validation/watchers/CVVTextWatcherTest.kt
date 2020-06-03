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
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.ValidationRuleHandler
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
import com.worldpay.access.checkout.validation.validators.CVVValidator
import org.junit.Before
import org.junit.Test

class CVVTextWatcherTest {

    private val validationRuleHandler = mock<ValidationRuleHandler>()
    private val validationResultHandler = mock<ValidationResultHandler>()

    private val pan = mock<EditText>()
    private val expiryMonth = mock<EditText>()
    private val expiryYear = mock<EditText>()
    private val cvv = mock<EditText>()

    private val cvvEditable = mock<Editable>()

    private lateinit var cvvTextWatcher: CVVTextWatcher

    @Before
    fun setup() {
        val cardDetailComponents = CardDetailComponents(
            pan = pan,
            expiryMonth = expiryMonth,
            expiryYear = expiryYear,
            cvv = cvv
        )

        cvvTextWatcher = CVVTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            cardDetailComponents = cardDetailComponents,
            cvvValidator = CVVValidator(),
            validationRuleHandler = validationRuleHandler,
            validationResultHandler = validationResultHandler
        )

        given(cvvEditable.toString()).willReturn("123")
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is identified`() {
        mockPan(VISA_PAN)

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(validationRuleHandler).handle(CVV, VISA_BRAND.cvv)
    }

    @Test
    fun `should pass validation rule to validation rule handler where brand is not identified`() {
        mockPan("000000")

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(validationRuleHandler).handle(CVV, CVV_RULE)
    }

    @Test
    fun `should pass validation result to validation result handler where brand is identified`() {
        mockPan(VISA_PAN)

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(validationResultHandler).handle(CVV, ValidationResult(partial = false, complete = true), VISA_BRAND)
    }

    @Test
    fun `should pass validation result to validation result handler where brand is not identified`() {
        mockPan("000000")

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(validationResultHandler).handle(CVV, ValidationResult(partial = true, complete = true), null)
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val cardDetailComponents = mock<CardDetailComponents>()
        val cvvValidator = mock<CVVValidator>()

        val cvvTextWatcher = CVVTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            cardDetailComponents = cardDetailComponents,
            cvvValidator = cvvValidator,
            validationRuleHandler = validationRuleHandler,
            validationResultHandler = validationResultHandler
        )

        cvvTextWatcher.beforeTextChanged("", 1, 2,3)
        cvvTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            cardDetailComponents,
            cvvValidator,
            validationResultHandler,
            validationRuleHandler
        )
    }

    private fun mockPan(value: String) {
        val panEditable = mock<Editable>()
        given(pan.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn(value)
    }

}
