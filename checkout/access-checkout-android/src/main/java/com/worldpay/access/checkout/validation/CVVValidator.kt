package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.CardBrandUtils.findCardBrandMatchingPAN
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor
import com.worldpay.access.checkout.validation.ValidatorUtils.regexMatches

class CVVValidator {

    fun validate(cvv: CVV, pan: String?, cardConfiguration: CardConfiguration): Pair<ValidationResult, CardBrand?> {

        if (!pan.isNullOrBlank()) {
            val (cardBrand, _) = findCardBrandMatchingPAN(cardConfiguration.brands, pan)
            if (cardBrand?.cvv != null) {
                return Pair(getValidationResultFor(cvv, cardBrand.cvv), cardBrand)
            }
        }

        return Pair(getDefaultValidationResult(cvv, cardConfiguration), null)
    }

    private fun getDefaultValidationResult(cvv: CVV, cardConfiguration: CardConfiguration): ValidationResult {
        if (!regexMatches(cardConfiguration.defaults.cvv.matcher, cvv)) {
            return ValidationResult(partial = false, complete = false)
        }
        return getValidationResultFor(cvv, cardConfiguration.defaults.cvv)
    }
}