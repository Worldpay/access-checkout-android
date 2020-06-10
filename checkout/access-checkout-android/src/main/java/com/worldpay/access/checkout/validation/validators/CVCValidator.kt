package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import java.util.concurrent.atomic.AtomicReference

internal class CVCValidator(
    private val cvvValidationResultHandler: CvvValidationResultHandler
) {

    private var cardValidationRule = AtomicReference(CVV_DEFAULTS)

    private val simpleValidator = SimpleValidator()

    fun validate(cvc: String, cardValidationRule: CardValidationRule = this.cardValidationRule.get()) {
        this.cardValidationRule.set(cardValidationRule)

        val result = simpleValidator.validate(cvc, this.cardValidationRule.get())

        cvvValidationResultHandler.handleResult(result)
    }

}
