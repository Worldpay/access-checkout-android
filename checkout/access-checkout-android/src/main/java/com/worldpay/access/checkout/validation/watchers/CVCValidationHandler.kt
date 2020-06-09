package com.worldpay.access.checkout.validation.watchers

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.validators.SimpleValidator

internal class CVCValidationHandler(
    private val cvvValidator: SimpleValidator,
    private val cvvValidationResultHandler: CvvValidationResultHandler,
    private var validationRule: CardValidationRule = DefaultCardRules.CVV_DEFAULTS
) {
    fun updateValidationRule(validationRule: CardValidationRule) {
        this.validationRule = validationRule
    }

    fun validate(cvc: String) {
        val result = cvvValidator.validate(cvc, validationRule)
        cvvValidationResultHandler.handleResult(result)
    }
}
