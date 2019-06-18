package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardValidationRule
import com.worldpay.access.checkout.validation.CardBrandUtils.findCardBrandMatchingPAN
import com.worldpay.access.checkout.validation.CardBrandUtils.validateAgainstMatcher
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor
import com.worldpay.access.checkout.validation.ValidatorUtils.isNumeric

/**
 * Validator for the pan field
 */
interface PANValidator {

    /**
     * Validates the pan field
     *
     * @param pan the pan to validate
     * @return a [Pair] of [ValidationResult] and [CardBrand] for the pan field
     */
    fun validate(pan: PAN): Pair<ValidationResult, CardBrand?>
}

internal class PANValidatorImpl(private val cardConfiguration: CardConfiguration?) : PANValidator {

    override fun validate(pan: PAN): Pair<ValidationResult, CardBrand?> {
        if (pan.isBlank() && cardConfiguration == null) {
            return Pair(ValidationResult(partial = true, complete = true), null)
        }

        val (cardBrand, cardBrandValidationRule) = findCardBrandMatchingPAN(cardConfiguration?.brands, pan)

        val validationRule: CardValidationRule = cardBrandValidationRule
                ?: cardConfiguration?.defaults?.pan
                ?: return Pair(ValidationResult(partial = true, complete = isLuhnValid(pan)), null)

        return Pair(getValidationResult(validationRule, pan), cardBrand)
    }

    private fun getValidationResult(validationRule: CardValidationRule, pan: PAN): ValidationResult {
        val validationResult = getValidationResultFor(pan, validationRule)
        val validMatcher = validateAgainstMatcher(pan, validationRule, true)

        val partial = validationResult.partial && validMatcher
        val complete = validationResult.complete && validMatcher && isLuhnValid(pan)

        return ValidationResult(partial, complete)
    }

    private fun isLuhnValid(pan: PAN): Boolean {
        if (!isNumeric(pan)) return false
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
