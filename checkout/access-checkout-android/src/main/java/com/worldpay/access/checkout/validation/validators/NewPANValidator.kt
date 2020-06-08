package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.NewValidatorUtils.getValidationResultFor

class NewPANValidator {

    fun validate(pan: String, cardValidationRule: CardValidationRule): Boolean {
        if (pan.isEmpty()) {
            return false
        }

        var validationResult = getValidationResultFor(pan, cardValidationRule)

        if (validationResult) {
            validationResult = isLuhnValid(pan)
        }

        return validationResult
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
