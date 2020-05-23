package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardValidationRule

internal object ValidatorUtils {

    fun isNumeric(number: String) = number.isNotBlank() && number.toCharArray().none { !it.isDigit() }

    fun regexMatches(regex: String, text: String): Boolean = regex.isNotBlank() && regex.toPattern().matcher(text).find()

    fun getValidationResultFor(text: String, cardValidationRule: CardValidationRule): ValidationResult {
        if (cardValidationRule.validLengths.isEmpty()) {
            return ValidationResult(partial = true, complete = true)
        }

        val validLengths: List<Int> = cardValidationRule.validLengths

        return ValidationResult(
            partial = text.length < validLengths.max() as Int,
            complete = validLengths.contains(text.length)
        )
    }

}
