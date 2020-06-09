package com.worldpay.access.checkout.validation.utils

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.CardBrandUtils.findBrandForPan

object ValidationRuleHelper {

    private val defaultMaxLength = 100

    fun getCvvValidationRule(pan: String, cardConfiguration: CardConfiguration): CardValidationRule {
        var rule = cardConfiguration.defaults.cvv
        if (pan.isNotBlank()) {
            val cardBrand = findBrandForPan(cardConfiguration, pan)
            if (cardBrand != null) {
                rule = cardBrand.cvv
            }
        }
        return rule
    }

    fun getPanValidationRule(cardBrand: CardBrand?, cardConfiguration: CardConfiguration): CardValidationRule {
        if (cardBrand == null) {
            return cardConfiguration.defaults.pan
        }
        return cardBrand.pan
    }

    fun getMaxLength(cardValidationRule: CardValidationRule) =
        cardValidationRule.validLengths.max() ?: defaultMaxLength
    
    
}
