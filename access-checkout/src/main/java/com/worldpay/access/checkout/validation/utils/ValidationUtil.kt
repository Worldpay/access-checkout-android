package com.worldpay.access.checkout.validation.utils

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider.getCardConfiguration

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
        cardValidationRule.validLengths.maxOrNull() ?: defaultMaxLength

    fun findBrandForPan(pan: String): RemoteCardBrand? {
        for (brand in getCardConfiguration().brands) {
            val unformattedPan = pan.replace("\\s+".toRegex(), "")
            if (brand.pan.matcher.toPattern().matcher(unformattedPan).find()) {
                return brand
            }
        }
        return null
    }

    fun isNumeric(number: String) = number.isNotBlank() && number.toCharArray().none { !it.isDigit() }
}
