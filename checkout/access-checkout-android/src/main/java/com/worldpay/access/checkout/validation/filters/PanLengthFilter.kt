package com.worldpay.access.checkout.validation.filters

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.utils.ValidationUtil

internal class PanLengthFilter(private val cardConfiguration: CardConfiguration): AbstractVariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        val cardBrand =
            ValidationUtil.findBrandForPan(cardConfiguration, source.toString())
        val cardValidationRule =
            ValidationUtil.getPanValidationRule(cardBrand, cardConfiguration)
        return ValidationUtil.getMaxLength(cardValidationRule)
    }

}
