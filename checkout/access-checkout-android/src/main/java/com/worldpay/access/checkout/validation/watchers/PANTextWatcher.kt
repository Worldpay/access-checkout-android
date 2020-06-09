package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvvValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class PANTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private var panValidator: NewPANValidator,
    private val cvcValidator: CVCValidator,
    private val cvvEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler
) : AbstractCardDetailTextWatcher() {

    private var cardBrand: CardBrand? = null

    override fun afterTextChanged(pan: Editable?) {
        val panText = pan.toString()
        val newCardBrand = findBrandForPan(cardConfiguration, panText)

        if (cardBrand != newCardBrand) {
            cardBrand = newCardBrand
            val cvvText = cvvEditText.text.toString()
            val cardValidationRule = getCvvValidationRule(cardBrand, cardConfiguration)
            cvcValidator.validate(cvvText, cardValidationRule)
        }

        val cardValidationRule = getPanValidationRule(cardBrand, cardConfiguration)

        val isValid = panValidator.validate(panText, cardValidationRule)
        panValidationResultHandler.handleResult(
            isValid = isValid,
            cardBrand = cardBrand
        )
    }

}
