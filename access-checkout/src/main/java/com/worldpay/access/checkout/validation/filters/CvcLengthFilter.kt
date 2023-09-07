package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider.Companion.getCardConfiguration
import com.worldpay.access.checkout.validation.utils.ValidationUtil
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvcValidationRule

internal class CvcLengthFilter(
    private val panAccessEditText: AccessEditText?
) : AbstractVariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        var validationRule = getCardConfiguration().defaults.cvc
        if (panAccessEditText != null) {
            val cardBrand = findBrandForPan(panAccessEditText.text)
            validationRule = getCvcValidationRule(cardBrand)
        }
        return ValidationUtil.getMaxLength(validationRule)
    }
}
