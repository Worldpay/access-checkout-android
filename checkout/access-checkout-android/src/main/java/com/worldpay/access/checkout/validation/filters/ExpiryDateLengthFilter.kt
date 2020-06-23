package com.worldpay.access.checkout.validation.filters

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.utils.ValidationUtil

internal class ExpiryDateLengthFilter(private val cardConfiguration: CardConfiguration): AbstractVariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        return ValidationUtil.getMaxLength(cardConfiguration.defaults.expiryDate)
    }

}
