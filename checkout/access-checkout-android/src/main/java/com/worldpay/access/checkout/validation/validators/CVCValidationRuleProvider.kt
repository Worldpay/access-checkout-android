package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import java.util.concurrent.atomic.AtomicReference

internal interface CardValidationRuleProvider {
    fun getRule(): CardValidationRule
}

internal class CVCValidationRuleManager : CardValidationRuleProvider {

    private val cvvValidationRule = AtomicReference(CVV_DEFAULTS)

    fun updateRule(cardValidationRule: CardValidationRule) {
        cvvValidationRule.set(cardValidationRule)
    }

    override fun getRule(): CardValidationRule {
        return cvvValidationRule.get()
    }

}
