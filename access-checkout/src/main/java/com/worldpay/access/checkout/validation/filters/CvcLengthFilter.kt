package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider.Companion.getCardConfiguration
import com.worldpay.access.checkout.validation.utils.ValidationUtil
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvcValidationRule

internal class CvcLengthFilter(
    private val panEditText: EditText?
) : AbstractVariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        var validationRule = getCardConfiguration().defaults.cvc
        if (panEditText != null) {
            val cardBrand = findBrandForPan(panEditText.text.toString())
            validationRule = getCvcValidationRule(cardBrand)
        }
        return ValidationUtil.getMaxLength(validationRule)
    }
}
