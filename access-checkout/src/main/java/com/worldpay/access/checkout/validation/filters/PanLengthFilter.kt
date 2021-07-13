package com.worldpay.access.checkout.validation.filters

import android.text.Spanned
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.utils.ValidationUtil
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import kotlin.math.ceil

internal class PanLengthFilter(
    private val enablePanFormatting: Boolean
) : AbstractVariableLengthFilter() {

    private val spacesAfterEveryXChars = 4.0

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source == null || dest == null) {
            return null
        }

        val text = getTextValue(dest.toString() + source.toString())
        val totalMaxLength = getMaxLength(text)
        val numberOfSpaces = getExpectedNumberOfSpaces(text)
        val maxLengthWithoutSpaces = totalMaxLength - numberOfSpaces

        return when {
            !text.contains(" ") && text.length > maxLengthWithoutSpaces -> {
                text.substring(0, maxLengthWithoutSpaces)
            }
            text.contains(" ") && text.length > totalMaxLength -> {
                text.substring(0, totalMaxLength)
            }
            else -> {
                text
            }
        }
    }

    override fun getMaxLength(source: CharSequence?): Int {
        val cardBrand = findBrandForPan(source.toString())
        val cardValidationRule = getPanValidationRule(cardBrand)

        val maxLength = ValidationUtil.getMaxLength(cardValidationRule)
        return maxLength + getExpectedNumberOfSpaces(source)
    }

    override fun getTextValue(source: String): String {
        return source.replace(Regex("[^0-9\\s]"), "")
    }

    private fun getExpectedNumberOfSpaces(source: CharSequence?): Int {
        if (enablePanFormatting) {
            val cardBrand = findBrandForPan(source.toString())
            val cardValidationRule = getPanValidationRule(cardBrand)

            return if (isAmex(cardBrand)) {
                2
            } else {
                val groups: Double = ValidationUtil.getMaxLength(cardValidationRule) / spacesAfterEveryXChars
                (ceil(groups) - 1).toInt()
            }
        }

        return 0
    }

    private fun isAmex(cardBrand: RemoteCardBrand?) =
        cardBrand != null && cardBrand.name.equals("amex", true)
}
