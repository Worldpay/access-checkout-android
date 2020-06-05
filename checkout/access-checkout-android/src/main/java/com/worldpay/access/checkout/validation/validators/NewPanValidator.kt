package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.CardBrandUtils
import com.worldpay.access.checkout.validation.NewValidatorUtils.getValidationResultFor

class NewPanValidator {

    fun validate(pan: String, cardConfiguration: CardConfiguration): Pair<Boolean, CardBrand?> {
        return if (pan.isEmpty()) {
             Pair(false, null)
        } else {
            val (cardBrand, cardBrandValidationRule) = CardBrandUtils.findCardBrandMatchingPAN(
                cardConfiguration.brands,
                pan
            )

            val validationRule: CardValidationRule = cardBrandValidationRule  ?: cardConfiguration.defaults.pan
            var validationResult = getValidationResultFor(pan, validationRule)

            if (validationResult) {
                validationResult = isLuhnValid(pan)
            }

            return Pair(validationResult, cardBrand)
        }
    }

    private fun isLuhnValid(pan: String): Boolean {
        var sum = 0
        var alternate = false
        for (i: Int in (pan.length - 1) downTo 0) {
            var n = Integer.parseInt(pan.substring(i, i + 1))
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = n % 10 + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}