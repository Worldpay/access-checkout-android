package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
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
    private val brandChangedHandler : BrandChangedHandler,
    private val cvcValidationRuleManager: CVCValidationRuleManager
) : AbstractCardDetailTextWatcher() {

    private var cardBrand: RemoteCardBrand? = null

    override fun afterTextChanged(pan: Editable?) {
        val panText = pan.toString()
        val newCardBrand = findBrandForPan(cardConfiguration, panText)

        handleCardBrandChange(newCardBrand)

        val cardValidationRule = getPanValidationRule(cardBrand, cardConfiguration)

        val isValid = panValidator.validate(panText, cardValidationRule)

        panValidationResultHandler.handleResult(isValid)
    }

    private fun handleCardBrandChange(newCardBrand: RemoteCardBrand?) {
        if (cardBrand != newCardBrand) {
            cardBrand = newCardBrand

            brandChangedHandler.handle(newCardBrand)

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
