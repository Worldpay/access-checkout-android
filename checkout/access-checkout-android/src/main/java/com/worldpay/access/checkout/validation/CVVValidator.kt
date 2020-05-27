package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.CardBrandUtils.findCardBrandMatchingPAN
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor

/**
 * Validator for the cvv field
 */
interface CVVValidator {

    /**
     * Validates the cvv field
     *
     * @param cvv the cvv to validate
     * @param pan (Optional) the pan field to validate against the cvv
     * @return a [Pair] of [ValidationResult] and [CardBrand] for the cvv field
     */
    fun validate(cvv: CVV, pan: PAN?): Pair<ValidationResult, CardBrand?>
}

internal class CVVValidatorImpl(
    private val cardConfiguration: CardConfiguration?
) : CVVValidator {

    override fun validate(cvv: CVV, pan: PAN?): Pair<ValidationResult, CardBrand?> {
        if (cvv.isBlank() && cardConfiguration == null) {
            return Pair(ValidationResult(partial = false, complete = false), null)
        }

        if (cardConfiguration == null) {
            return Pair(ValidationResult(partial = true, complete = true), null)
        }

        if (!pan.isNullOrBlank()) {
            val (cardBrand, _) = findCardBrandMatchingPAN(cardConfiguration.brands, pan)
            if (cardBrand?.cvv != null) {
                return Pair(getValidationResultFor(cvv, cardBrand.cvv), cardBrand)
            }
        }

        return Pair(getDefaultValidationResult(cvv, cardConfiguration), null)
    }

    private fun getDefaultValidationResult(cvv: CVV, cardConfiguration: CardConfiguration): ValidationResult {
        return getValidationResultFor(cvv, cardConfiguration.defaults.cvv)
    }
}