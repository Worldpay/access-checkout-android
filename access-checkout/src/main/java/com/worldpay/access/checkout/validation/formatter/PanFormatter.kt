package com.worldpay.access.checkout.validation.formatter

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.utils.ValidationUtil
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import kotlin.math.ceil

internal class PanFormatter(
    private val enablePanFormatting: Boolean
) {
    // RegEx for Amex brand
    private val AMEX_ALLOWED_PATTERNS = listOf(
        "^\\d{0,4}$", // covers 1 group with 0 to 4 digits
        "^(\\d{4}) ?$", // covers 1 group of exactly 4 digits + an optional trailing space
        "^(\\d{4}) (\\d{1,6})$", // covers 1 group of exactly of 4 digits + 1 group with 1 to 6 digits
        "^(\\d{4}) (\\d{6}) ?$", // covers 1 group of exactly of 4 digits + 1 group with of exactly 6 digits + an optional trailing space
        "^(\\d{4}) (\\d{6}) (\\d{1,5})$") // covers 1 group of exactly of 4 digits + 1 group with of exactly 6 digits + 1 group with 1 to 5 digits
        .map(String::toRegex)

    // RegEx for all other brands
    // covers up to 5 groups of 4 digits separated by a space + ability to enter a single group of up to 4 digits
    private val OTHER_BRANDS_ALLOWED_PATTERN = "^((\\d{4} ){0,4})(\\d{0,4})?$".toRegex()

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

        if (panWithNoSpaces.length > 4) {
            val chunksOfSix = panWithNoSpaces.removeRange(0, 4).chunked(6)
            formattedPan += " ${chunksOfSix[0].trim()}"

            if (chunksOfSix.drop(1).isNotEmpty()) {
                formattedPan += " ${chunksOfSix.drop(1).joinToString("").trim()}"
            }
        }

        return formattedPan
    }

    private fun removeSpaces(pan: String) = pan.replace("\\s+".toRegex(), "")

    private fun requiresFormatting(pan: String, brand: RemoteCardBrand?): Boolean {
        if (isAmex(brand)) {
            for (regEx in AMEX_ALLOWED_PATTERNS) {
                if (regEx.matches(pan)) {
                    return false
                }
            }
            return true
        } else {
            return !OTHER_BRANDS_ALLOWED_PATTERN.matches(pan)
        }
    }

    private fun isAmex(cardBrand: RemoteCardBrand?) =
        cardBrand != null && cardBrand.name.equals("amex", true)
}
