package com.worldpay.access.checkout.validation.utils

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand

internal object ValidationUtil {

    private const val defaultMaxLength = 100

    fun getCvcValidationRule(cardBrand: RemoteCardBrand?, cardConfiguration: CardConfiguration): CardValidationRule {
        if (cardBrand == null) {
            return cardConfiguration.defaults.cvc
        }
        return cardBrand.cvc
    }

    fun getPanValidationRule(cardBrand: RemoteCardBrand?, cardConfiguration: CardConfiguration): CardValidationRule {
        if (cardBrand == null) {
            return cardConfiguration.defaults.pan
        }
        return cardBrand.pan
    }

    fun getMaxLength(cardValidationRule: CardValidationRule) =
        cardValidationRule.validLengths.max() ?: defaultMaxLength

    fun findBrandForPan(cardConfiguration: CardConfiguration, pan: String) : RemoteCardBrand? {
        for (brand in cardConfiguration.brands) {
            if (brand.pan.matcher.toPattern().matcher(pan).find()) {
                return brand
            }
        }
        return null
    }

    fun isNumeric(number: String) = number.isNotBlank() && number.toCharArray().none { !it.isDigit() }

}
