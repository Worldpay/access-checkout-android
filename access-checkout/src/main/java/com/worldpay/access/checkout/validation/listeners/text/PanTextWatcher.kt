package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvcValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.PanValidator

internal class PanTextWatcher(
    private var panValidator: PanValidator,
    private val cvcValidator: CvcValidator,
    private val cvcEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler,
    private val brandChangedHandler : BrandChangedHandler,
    private val cvcValidationRuleManager: CVCValidationRuleManager
) : AbstractCardDetailTextWatcher() {

    private var cardBrand: RemoteCardBrand? = null

    override fun afterTextChanged(pan: Editable?) {
        val panText = pan.toString()
        val newCardBrand = findBrandForPan(panText)

        handleCardBrandChange(newCardBrand)

        val cardValidationRule = getPanValidationRule(newCardBrand)

        val isValid = panValidator.validate(panText, cardValidationRule, newCardBrand)

        panValidationResultHandler.handleResult(isValid)
    }

    private fun handleCardBrandChange(newCardBrand: RemoteCardBrand?) {
        if (cardBrand != newCardBrand) {
            cardBrand = newCardBrand

            brandChangedHandler.handle(newCardBrand)

            updateCvcValidationRule()

            val cvcText = cvcEditText.text.toString()
            if (cvcText.isNotBlank()) {
                cvcValidator.validate(cvcText)
            }
        }
    }

    private fun updateCvcValidationRule() {
        val cardValidationRule = getCvcValidationRule(cardBrand)
        cvcValidationRuleManager.updateRule(cardValidationRule)
    }

}
