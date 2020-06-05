package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidatorUtils.regexMatches

internal object CardBrandUtils {

    fun findBrandForPan(cardConfiguration: CardConfiguration, pan: String) : CardBrand? {
        for (brand in cardConfiguration.brands) {
            if (regexMatches(brand.pan.matcher, pan)) {
                return brand
            }
        }
        return null
    }

}
