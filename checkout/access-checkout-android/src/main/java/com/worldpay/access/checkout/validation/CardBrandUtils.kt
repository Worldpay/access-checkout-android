package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardValidationRule

internal object CardBrandUtils {

    fun findCardBrandMatchingPAN(cardBrands: List<CardBrand>, pan: PAN): Pair<CardBrand?, CardValidationRule?> {
        var cardBrandValidationRule: CardValidationRule? = null
        val cardBrand: CardBrand? = cardBrands.firstOrNull {
            cardBrandValidationRule = cardValidationRule(it, pan)
            cardBrandValidationRule != null
        }

        return Pair(cardBrand, cardBrandValidationRule)
    }

    fun validateAgainstMatcher(pan: PAN, cardValidationRule: CardValidationRule): Boolean {
        return ValidatorUtils.regexMatches(cardValidationRule.matcher, pan)
    }

    fun cardValidationRule(cardBrand: CardBrand, pan: PAN): CardValidationRule? {
        val panRule = cardBrand.pan
        return if (panRule != null && validateAgainstMatcher(pan, panRule)) {
            panRule
        } else {
            null
        }
    }
}
