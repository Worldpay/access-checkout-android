package com.worldpay.access.checkout.validation.formatter

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.utils.ValidationUtil
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import kotlin.math.ceil

internal class PanFormatter(
    private val enablePanFormatting: Boolean
) {

    private val spacesAfterEveryXChars = 4.0

    fun format(pan: String, brand: RemoteCardBrand?): String {
        if (!enablePanFormatting) {
            return pan
        }

        if (requiresFormatting(pan, brand)) {
            val cardValidationRule = getPanValidationRule(brand)
            val maxLength = ValidationUtil.getMaxLength(cardValidationRule)

            var panWithNoSpaces = removeSpaces(pan)
            if (panWithNoSpaces.length > maxLength) {
                panWithNoSpaces = panWithNoSpaces.substring(0, maxLength)
            }

            return if (isAmex(brand)) {
                formatAmexPan(panWithNoSpaces)
            } else {
                panWithNoSpaces
                    .replace("(.{4})".toRegex(), "$1 ")
                    .trim()
            }
        }

        return pan
    }

    fun getExpectedNumberOfSpaces(pan: String): Int {
        if (enablePanFormatting) {
            val cardBrand = findBrandForPan(pan)
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

    fun isFormattingEnabled() = enablePanFormatting

    private fun formatAmexPan(pan: String): String {
        val panWithNoSpaces = removeSpaces(pan)
        var formattedPan = panWithNoSpaces.chunked(4)[0]
        val chunksOfSix = panWithNoSpaces.removeRange(0, 4).chunked(6)

        formattedPan += " ${chunksOfSix[0].trim()}"

        if (chunksOfSix.drop(1).isNotEmpty()) {
            formattedPan += " ${chunksOfSix.drop(1).joinToString("").trim()}"
        }

        return formattedPan
    }

    private fun removeSpaces(pan: String) = pan.replace("\\s+".toRegex(), "")

    private fun requiresFormatting(pan: String, brand: RemoteCardBrand?): Boolean {
        var requiresFormatting = false
        val splitPan = pan.trim().split(" ")

        if (isAmex(brand)) {
            if (splitPan[0].length != 4 && pan.length > 4) {
                return true
            }
            if (splitPan.size == 2 && splitPan[1].length > 6) {
                return true
            }
            if (splitPan.size == 3 && splitPan[1].length != 6) {
                return true
            }
            if (splitPan.size > 3) {
                return true
            }
        } else {
            for (s in splitPan) {
                if (s.length != 4) {
                    requiresFormatting = true
                }
            }
        }
        return requiresFormatting
    }

    private fun isAmex(cardBrand: RemoteCardBrand?) =
        cardBrand != null && cardBrand.name.equals("amex", true)
}
