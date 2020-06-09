package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule

internal class SimpleValidator {

    fun validate(text: String, cardValidationRule: CardValidationRule) : Boolean {
        if (text.isBlank()) {
            return false
        }

        if (!cardValidationRule.matcher.toPattern().matcher(text).find()) {
            return false
        }

        if (cardValidationRule.validLengths.isEmpty()) {
            return true
        }

        return cardValidationRule.validLengths.contains(text.length)
    }

}
