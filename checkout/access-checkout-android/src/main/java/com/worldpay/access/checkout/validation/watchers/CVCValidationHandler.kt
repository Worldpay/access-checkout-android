package com.worldpay.access.checkout.validation.watchers

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.validators.SimpleValidator

internal class CVCValidationHandler(
    private val cvvValidator: SimpleValidator,
    private val cvvValidationResultHandler: CvvValidationResultHandler,
    private var cardValidationRule: CardValidationRule = DefaultCardRules.CVV_DEFAULTS
) {
    fun updateCvcRuleAndValidate(cvc: String, cvvRule: CardValidationRule?) {
        if (cvvRule != null) {
            this.cardValidationRule = cvvRule
        } else {
            this.cardValidationRule = DefaultCardRules.CVV_DEFAULTS
        }

        validate(cvc)
    }

    fun getRule() : CardValidationRule {
        return cardValidationRule
    }

    fun validate(cvc: String) {
        val result = cvvValidator.validate(cvc.toString(), cardValidationRule)
        cvvValidationResultHandler.handleResult(result)
    }
}
