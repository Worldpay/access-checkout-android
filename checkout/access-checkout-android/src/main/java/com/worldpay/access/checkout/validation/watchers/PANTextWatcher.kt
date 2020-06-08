package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.CardBrandUtils.findBrandForPan
import com.worldpay.access.checkout.validation.InputFilter
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class PANTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private var panValidator: NewPANValidator,
    private val inputFilter: InputFilter,
    private val panEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler
) : AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(pan: Editable?) {
        val panText = pan.toString()
        val cardBrand = findBrandForPan(cardConfiguration, panText)
        val cardValidationRule = getValidationRule(cardBrand, cardConfiguration)

        inputFilter.filter(
            editText = panEditText,
            cardValidationRule = cardValidationRule
        )

        val isValid = panValidator.validate(panText, cardValidationRule)
        panValidationResultHandler.handleResult(
            isValid = isValid,
            cardBrand = cardBrand
        )
    }

    private fun getValidationRule(cardBrand: CardBrand?, cardConfiguration: CardConfiguration): CardValidationRule {
        if (cardBrand == null) {
            return cardConfiguration.defaults.pan
        }
        return cardBrand.pan
    }

}
