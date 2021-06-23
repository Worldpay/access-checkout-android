package com.worldpay.access.checkout.validation.filters

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.utils.ValidationUtil
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import kotlin.math.ceil

internal class PanLengthFilter(
    private val enablePanFormatting: Boolean
) : AbstractVariableLengthFilter() {

    private val spacesAfterEveryXChars = 4.0

    override fun getMaxLength(source: CharSequence?): Int {
        val cardBrand = findBrandForPan(source.toString())
        val cardValidationRule = getPanValidationRule(cardBrand)

        val maxLength = ValidationUtil.getMaxLength(cardValidationRule)
        var numberOfSpaces = 0

        if (enablePanFormatting) {
            numberOfSpaces = if (isAmex(cardBrand)) {
                2
            } else {
                val groups: Double = maxLength / spacesAfterEveryXChars
                (ceil(groups) - 1).toInt()
            }
        }

        return maxLength + numberOfSpaces
    }

    private fun isAmex(cardBrand: RemoteCardBrand?) =
        cardBrand != null && cardBrand.name.equals("amex", true)
}
