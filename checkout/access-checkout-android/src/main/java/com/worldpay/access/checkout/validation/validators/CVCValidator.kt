package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler

internal class CVCValidator(
    private val cvvValidationResultHandler: CvvValidationResultHandler,
    private val cardValidationRuleProvider: CardValidationRuleProvider
) {

    private val simpleValidator = SimpleValidator()

    fun validate(cvc: String) {
        val result = simpleValidator.validate(cvc, cardValidationRuleProvider.getRule())

        cvvValidationResultHandler.handleResult(result)
    }

}
