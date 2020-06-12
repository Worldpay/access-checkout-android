package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor
import com.worldpay.access.checkout.validation.ValidatorUtils.regexMatches
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan

@Deprecated(message = "legacy")
class PANValidator {

    fun getValidationRule(pan: String, cardConfiguration: CardConfiguration): CardValidationRule {
        val cardBrand = findBrandForPan(cardConfiguration, pan) ?: return cardConfiguration.defaults.pan
        return cardBrand.pan
    }

    fun validate(pan: String, cardConfiguration: CardConfiguration): Pair<ValidationResult, RemoteCardBrand?> {
        val cardBrand = findBrandForPan(cardConfiguration, pan)
        var validationRule = cardConfiguration.defaults.pan
        if (cardBrand != null) {
            validationRule = cardBrand.pan
        }

        return Pair(getValidationResult(validationRule, pan), cardBrand)
    }

    private fun getValidationResult(validationRule: CardValidationRule, pan: String): ValidationResult {
        val validationResult = getValidationResultFor(pan, validationRule)
        val validMatcher = regexMatches(validationRule.matcher, pan)

        val partial = validationResult.partial && validMatcher
        val complete = validationResult.complete && validMatcher && isLuhnValid(pan)

        return ValidationResult(
            partial,
            complete
        )
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
