package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardValidationRule

internal object ValidatorUtils {

    fun isNumeric(number: String) = number.isNotBlank() && number.toCharArray().none { !it.isDigit() }

    fun regexMatches(regex: String, text: String): Boolean = regex.isNotBlank() && regex.toPattern().matcher(text).find()

    fun getValidationResultFor(text: String, cardValidationRule: CardValidationRule): ValidationResult {
        cardValidationRule.let { rule ->
            return when {
                rule.validLength != null -> {
                    val validLength: Int = rule.validLength
                    ValidationResult(
                        partial = text.length < validLength,
                        complete = text.length == validLength
                    )
                }
                rule.minLength != null && rule.maxLength != null -> ValidationResult(
                    partial = text.length < rule.minLength,
                    complete = text.length >= rule.minLength && text.length <= rule.maxLength
                )
                rule.minLength != null -> ValidationResult(
                    partial = text.length < rule.minLength,
                    complete = text.length >= rule.minLength
                )
                rule.maxLength != null -> ValidationResult(
                    partial = text.length <= rule.maxLength,
                    complete = text.length <= rule.maxLength
                )
                else -> ValidationResult(partial = true, complete = true)
            }
        }
    }

}
