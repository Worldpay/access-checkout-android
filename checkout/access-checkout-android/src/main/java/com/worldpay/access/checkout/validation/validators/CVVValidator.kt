package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.CVV
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor
import com.worldpay.access.checkout.validation.ValidatorUtils.regexMatches
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan

class CVVValidator {

    fun getValidationRule(pan: String, cardConfiguration: CardConfiguration): CardValidationRule {
        var rule = cardConfiguration.defaults.cvv
        if (pan.isNotBlank()) {
            val cardBrand = findBrandForPan(cardConfiguration, pan)
            if (cardBrand != null) {
                rule = cardBrand.cvv
            }
        }
        return rule
    }

    fun validate(cvv: CVV, pan: String?, cardConfiguration: CardConfiguration): Pair<ValidationResult, CardBrand?> {
        if (!pan.isNullOrBlank()) {
            val cardBrand = findBrandForPan(cardConfiguration, pan)
            if (cardBrand != null) {
                return Pair(getValidationResultFor(cvv, cardBrand.cvv), cardBrand)
            }
        }

        return Pair(getDefaultValidationResult(cvv, cardConfiguration), null)
    }

    private fun getDefaultValidationResult(cvv: CVV, cardConfiguration: CardConfiguration): ValidationResult {
        if (!regexMatches(cardConfiguration.defaults.cvv.matcher, cvv)) {
            return ValidationResult(
                partial = false,
                complete = false
            )
        }
        return getValidationResultFor(cvv, cardConfiguration.defaults.cvv)
    }

}
