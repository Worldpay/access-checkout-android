package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.utils.ValidationUtil

internal class CvcLengthFilter(
    private val panEditText: EditText?,
    private val cardConfiguration: CardConfiguration
): AbstractVariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        var validationRule = cardConfiguration.defaults.cvc
        if (panEditText != null) {
            val cardBrand = ValidationUtil.findBrandForPan(
                cardConfiguration,
                panEditText.text.toString()
            )
            validationRule =
                ValidationUtil.getCvcValidationRule(cardBrand, cardConfiguration)
        }
        return ValidationUtil.getMaxLength(validationRule)
    }

}
