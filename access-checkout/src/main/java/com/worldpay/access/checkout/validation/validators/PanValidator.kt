package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.*

internal class PanValidator(private val acceptedCardBrands: Array<String>) {

    private val simpleValidator = SimpleValidator()

    fun validate(text: String, cardValidationRule: CardValidationRule, cardBrand: RemoteCardBrand?): PanValidationResult {
        val isAcceptedCardBrand = isAcceptedCardBrand(cardBrand)

        if (!isAcceptedCardBrand) {
            return CARD_BRAND_NOT_ACCEPTED
        }

        val isValid = simpleValidator.validate(text, cardValidationRule)

        if (!isValid) {
            return INVALID
        }

        if (!isLuhnValid(text)) {
            return INVALID_LUHN
        }

        return VALID
    }

    private fun isAcceptedCardBrand(cardBrand: RemoteCardBrand?): Boolean {
        if (acceptedCardBrands.isEmpty() || cardBrand == null) {
            return true
        }

        for (acceptedCardBrand in acceptedCardBrands) {
            if (acceptedCardBrand.equals(cardBrand.name, true)) {
                return true
            }
        }

        return false
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

    internal enum class PanValidationResult {
        VALID,
        INVALID,
        CARD_BRAND_NOT_ACCEPTED,
        INVALID_LUHN
    }

}
