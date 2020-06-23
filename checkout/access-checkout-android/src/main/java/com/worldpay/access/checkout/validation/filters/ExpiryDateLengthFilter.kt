package com.worldpay.access.checkout.validation.filters

import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider.Companion.getCardConfiguration
import com.worldpay.access.checkout.validation.utils.ValidationUtil

internal class ExpiryDateLengthFilter : AbstractVariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        return ValidationUtil.getMaxLength(getCardConfiguration().defaults.expiryDate)
    }

}
