package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.validation.result.handler.CvcValidationResultHandler

internal class CvcValidator(
    private val cvcValidationResultHandler: CvcValidationResultHandler,
    private val cardValidationRuleProvider: CardValidationRuleProvider
) {

    private val simpleValidator = SimpleValidator()

    fun validate(cvc: String) {
        val result = simpleValidator.validate(cvc, cardValidationRuleProvider.getRule())

        cvcValidationResultHandler.handleResult(result)
    }

}
