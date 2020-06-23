package com.worldpay.access.checkout.validation.filters

import com.worldpay.access.checkout.validation.utils.ValidationUtil
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule

internal class PanLengthFilter : AbstractVariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        val cardBrand = findBrandForPan(source.toString())
        val cardValidationRule = getPanValidationRule(cardBrand)
        return ValidationUtil.getMaxLength(cardValidationRule)
    }

}
