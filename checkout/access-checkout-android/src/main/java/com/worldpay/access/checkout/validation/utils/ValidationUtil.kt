package com.worldpay.access.checkout.validation.utils

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.ValidatorUtils

object ValidationUtil {

    private const val defaultMaxLength = 100

    fun getCvvValidationRule(cardBrand: CardBrand?, cardConfiguration: CardConfiguration): CardValidationRule {
        if (cardBrand == null) {
            return cardConfiguration.defaults.cvv
        }
        return cardBrand.cvv
    }

    fun getPanValidationRule(cardBrand: CardBrand?, cardConfiguration: CardConfiguration): CardValidationRule {
        if (cardBrand == null) {
            return cardConfiguration.defaults.pan
        }
        return cardBrand.pan
    }

    fun getMaxLength(cardValidationRule: CardValidationRule) =
        cardValidationRule.validLengths.max() ?: defaultMaxLength

    fun findBrandForPan(cardConfiguration: CardConfiguration, pan: String) : CardBrand? {
        for (brand in cardConfiguration.brands) {
            if (ValidatorUtils.regexMatches(brand.pan.matcher, pan)) {
                return brand
            }
        }
        return null
    }

}
