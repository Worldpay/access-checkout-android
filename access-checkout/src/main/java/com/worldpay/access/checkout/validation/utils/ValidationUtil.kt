package com.worldpay.access.checkout.validation.utils

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider.Companion.getCardConfiguration

internal object ValidationUtil {

    private const val defaultMaxLength = 100

    fun getCvcValidationRule(cardBrand: RemoteCardBrand?): CardValidationRule {
        if (cardBrand == null) {
            return getCardConfiguration().defaults.cvc
        }
        return cardBrand.cvc
    }

    fun getPanValidationRule(cardBrand: RemoteCardBrand?): CardValidationRule {
        if (cardBrand == null) {
            return getCardConfiguration().defaults.pan
        }
        return cardBrand.pan
    }

    fun getMaxLength(cardValidationRule: CardValidationRule) =
        cardValidationRule.validLengths.max() ?: defaultMaxLength

    fun findBrandForPan(pan: String) : RemoteCardBrand? {
        for (brand in getCardConfiguration().brands) {
            if (brand.pan.matcher.toPattern().matcher(pan).find()) {
                return brand
            }
        }
        return null
    }

    fun isNumeric(number: String) = number.isNotBlank() && number.toCharArray().none { !it.isDigit() }

}
