package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule

internal class NewPANValidator {

    private val simpleValidator = SimpleValidator()

    fun validate(text: String, cardValidationRule: CardValidationRule): Boolean {
        val isValid = simpleValidator.validate(text, cardValidationRule)

        if (isValid) {
            return isLuhnValid(text)
        }

        return isValid
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
