package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardValidationRule

internal object NewValidatorUtils {

    fun isNumeric(number: String) = number.isNotBlank() && number.toCharArray().none { !it.isDigit() }

    fun regexMatches(regex: String, text: String): Boolean = regex.isNotBlank() && regex.toPattern().matcher(text).find()

    fun getValidationResultFor(text: String, cardValidationRule: CardValidationRule): Boolean {
        if (cardValidationRule.validLengths.isEmpty()) {
            return true
        }

        val validLengths: List<Int> = cardValidationRule.validLengths

        return validLengths.contains(text.length) && regexMatches(cardValidationRule.matcher, text)

    }

}
