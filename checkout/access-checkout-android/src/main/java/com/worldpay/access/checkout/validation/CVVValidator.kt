package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.CardBrandUtils.findCardBrandMatchingPAN
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor

interface CVVValidator {

    fun validate(cvv: CVV, pan: PAN?): Pair<ValidationResult, CardBrand?>
}

internal class CVVValidatorImpl(
    private val cardConfiguration: CardConfiguration
) : CVVValidator {

    private val defaultValidationResult = ValidationResult(partial = true, complete = true)

    override fun validate(cvv: CVV, pan: PAN?): Pair<ValidationResult, CardBrand?> {
        if (cardConfiguration.isEmpty()) {
            return Pair(ValidationResult(partial = true, complete = true), null)
        }

        if (pan != null && pan.isNotBlank()) {
            val (cardBrand, _) = findCardBrandMatchingPAN(cardConfiguration.brands, pan)
            if (cardBrand?.cvv != null) {
                return Pair(getValidationResultFor(cvv, cardBrand.cvv), cardBrand)
            }
        }

        return Pair(getDefaultValidationResult(cvv), null)
    }

    private fun getDefaultValidationResult(cvv: CVV): ValidationResult {
        return if (cardConfiguration.defaults != null) {
            val defaultRule = cardConfiguration.defaults.cvv ?: return defaultValidationResult
            getValidationResultFor(cvv, defaultRule)
        } else defaultValidationResult
    }
}