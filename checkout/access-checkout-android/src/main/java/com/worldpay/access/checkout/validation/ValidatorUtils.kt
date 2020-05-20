package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardValidationRule

internal object ValidatorUtils {

    fun isNumeric(number: String) = number.isNotBlank() && number.toCharArray().none { !it.isDigit() }

    fun regexMatches(regex: String, text: String): Boolean = regex.isNotBlank() && regex.toPattern().matcher(text).find()

    fun getValidationResultFor(text: String, cardValidationRule: CardValidationRule): ValidationResult {
        cardValidationRule.let { rule ->
            return when {
                rule.validLengths.isNotEmpty() -> {
                    val validLengths: List<Int> = rule.validLengths
                    ValidationResult(
                        partial = text.length < validLengths.max() as Int,
                        complete = validLengths.contains(text.length)
                    )
                }
                else -> ValidationResult(partial = true, complete = true)
            }
        }
    }

}
