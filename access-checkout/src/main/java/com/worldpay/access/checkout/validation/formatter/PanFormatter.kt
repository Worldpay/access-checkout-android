package com.worldpay.access.checkout.validation.formatter

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand

internal class PanFormatter(
    private val disablePanFormatting: Boolean
) {

    fun format(pan: String, brand: RemoteCardBrand?): String {
        if (disablePanFormatting) {
            return pan
        }

        if (requiresFormatting(pan, brand)) {
            return if (isAmex(brand)) {
                formatAmexPan(pan)
            } else {
                pan
                    .replace("\\s+".toRegex(), "")
                    .replace("(.{4})".toRegex(), "$1 ")
                    .trim()
            }
        }

        return pan
    }

    private fun formatAmexPan(pan: String): String {
        val panWithNoSpaces = pan.replace("\\s+".toRegex(), "")
        var formattedPan = panWithNoSpaces.chunked(4)[0]
        val chunksOfSix = panWithNoSpaces.removeRange(0, 4).chunked(6)

        formattedPan += " ${chunksOfSix[0].trim()}"

        if (chunksOfSix.drop(1).isNotEmpty()) {
            formattedPan += " ${chunksOfSix.drop(1).joinToString("").trim()}"
        }

        return formattedPan
    }

    private fun requiresFormatting(pan: String, brand: RemoteCardBrand?): Boolean {
        var requiresFormatting = false
        val splitPan = pan.trim().split(" ")

        if (isAmex(brand)) {
            if (splitPan[0].length > 4) {
                return true
            }
            if (splitPan.size >= 2 && splitPan[1].length > 6) {
                return true
            }
            if (splitPan.size >= 3 && splitPan[2].length > 5) {
                return true
            }
        } else {
            for (s in splitPan) {
                if (s.length > 4) {
                    requiresFormatting = true
                }
            }
        }
        return requiresFormatting
    }

    private fun isAmex(cardBrand: RemoteCardBrand?) =
        cardBrand != null && cardBrand.name.equals("amex", true)
}
