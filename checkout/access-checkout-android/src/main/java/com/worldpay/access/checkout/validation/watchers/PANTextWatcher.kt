package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class PANTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private var panValidator: NewPANValidator,
    private val cvvEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler,
    private val cvcValidationHandler: CVCValidationHandler
) : AbstractCardDetailTextWatcher() {

    private var cardBrand: CardBrand? = null

    override fun afterTextChanged(pan: Editable?) {
        val panText = pan.toString()
        var cvvText = ""
        val newCardBrand = findBrandForPan(cardConfiguration, panText)

        if (cardBrand != newCardBrand) {
            cardBrand = newCardBrand

            if (cvvEditText.text != null) {
                cvvText = cvvEditText.text.toString()
            }

            cvcValidationHandler.updateCvcRuleAndValidate(cvvText, cardBrand?.cvv)
        }

        val cardValidationRule = getValidationRule(cardBrand, cardConfiguration)

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
