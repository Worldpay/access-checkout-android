package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvvValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class PANTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private var panValidator: NewPANValidator,
    private val cvcValidator: CVCValidator,
    private val cvvEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler,
    private val cvcValidationRuleManager: CVCValidationRuleManager
) : AbstractCardDetailTextWatcher() {

    private var cardBrand: CardBrand? = null

    override fun afterTextChanged(pan: Editable?) {
        val panText = pan.toString()
        val newCardBrand = findBrandForPan(cardConfiguration, panText)

        handleCardBrandChange(newCardBrand)

        val cardValidationRule = getPanValidationRule(cardBrand, cardConfiguration)

        val isValid = panValidator.validate(panText, cardValidationRule)
        panValidationResultHandler.handleResult(
            isValid = isValid,
            cardBrand = cardBrand
        )
    }

    private fun handleCardBrandChange(newCardBrand: CardBrand?) {
        if (cardBrand != newCardBrand) {
            cardBrand = newCardBrand

            updateCvcValidationRule()

            val cvvText = cvvEditText.text.toString()
            if (cvvText.isNotBlank()) {
                cvcValidator.validate(cvvText)
            }
        }
    }

    private fun updateCvcValidationRule() {
        val cardValidationRule = getCvvValidationRule(cardBrand, cardConfiguration)
        cvcValidationRuleManager.updateRule(cardValidationRule)
    }

}
