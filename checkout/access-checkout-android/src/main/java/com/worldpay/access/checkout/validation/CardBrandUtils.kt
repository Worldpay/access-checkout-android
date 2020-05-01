package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardValidationRule

internal object CardBrandUtils {

    fun findCardBrandMatchingPAN(cardBrands: List<CardBrand>?, pan: PAN): Pair<CardBrand?, CardValidationRule?> {
        var cardBrandValidationRule: CardValidationRule? = null
        val cardBrand: CardBrand? = cardBrands?.firstOrNull {
            cardBrandValidationRule = cardValidationRule(it, pan)
            cardBrandValidationRule != null
        }

        return Pair(cardBrand, cardBrandValidationRule)
    }

    fun validateAgainstMatcher(pan: PAN, cardValidationRule: CardValidationRule, default: Boolean): Boolean {
        val regex = cardValidationRule.matcher ?: return default
        return ValidatorUtils.regexMatches(regex, pan)
    }

    fun cardValidationRule(cardBrand: CardBrand, pan: PAN): CardValidationRule? {
        val panRule = cardBrand.pans
        if (panRule != null && validateAgainstMatcher(pan, panRule, false)) {
            return panRule
        } else {
            return null
        }
    }
}
