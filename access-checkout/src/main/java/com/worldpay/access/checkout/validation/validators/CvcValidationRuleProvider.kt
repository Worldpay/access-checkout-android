package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVC_DEFAULTS
import java.util.concurrent.atomic.AtomicReference

internal interface CardValidationRuleProvider {
    fun getRule(): CardValidationRule
}

internal class CVCValidationRuleManager : CardValidationRuleProvider {

    private val cvcValidationRule = AtomicReference(CVC_DEFAULTS)

    fun updateRule(cardValidationRule: CardValidationRule) {
        cvcValidationRule.set(cardValidationRule)
    }

    override fun getRule(): CardValidationRule {
        return cvcValidationRule.get()
    }
}
