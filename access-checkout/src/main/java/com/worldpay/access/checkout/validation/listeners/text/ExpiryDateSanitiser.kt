package com.worldpay.access.checkout.validation.listeners.text

import com.worldpay.access.checkout.api.configuration.DefaultCardRules.EXPIRY_DATE_DEFAULTS
import com.worldpay.access.checkout.validation.utils.ValidationUtil.isNumeric

internal class ExpiryDateSanitiser {

    private val nonNumericRegex = Regex("[^0-9/]")

    internal companion object {
        const val SEPARATOR = "/"
    }

    fun sanitise(str: String): String {
        // do not sanitise empty strings
        if (str.isBlank()) {
            return str
        }

        var expiryDate = str.replace("/", "")

        expiryDate = stripNonNumericalCharacters(expiryDate)

        if (expiryDate.isEmpty()) {
            return expiryDate
        }

        if (expiryDate.length == 1) {
            val month = getFormattedMonth(expiryDate)
            return if (month.length > 1) {
                month.plus(SEPARATOR)
            } else {
                month
            }
        }

        return reformatDate(expiryDate)
    }

    private fun stripNonNumericalCharacters(expiryDate: String): String {
        if (!isNumeric(expiryDate)) {
            return expiryDate.replace(nonNumericRegex, "")
        }
        return expiryDate
    }

    private fun reformatDate(expiryDate: String): String {
        val month = getFormattedMonth(expiryDate)
        val year = getFormattedYear(expiryDate)

        return limitToMaxLength(month.plus(SEPARATOR).plus(year))
    }

    private fun getFormattedMonth(expiryDate: String): String {
        if (expiryDate.length == 1) {
            val month = expiryDate.substring(0, 1)

            if (month.toInt() >= 2) {
                return month.padStart(2, '0')
            }

            return month
        } else {
            val month = expiryDate.substring(0, 2)

            if (month.toInt() >= 13) {
                return month.substring(0, 1).padStart(2, '0')
            }

            return month
        }
    }

    private fun getFormattedYear(expiryDate: String): String {
        val month = expiryDate.substring(0, 2)

        if (month.toInt() >= 13) {
            return expiryDate.substring(1)
        }
        return expiryDate.substring(2)
    }

    private fun limitToMaxLength(expiryDate: String): String {
        if (expiryDate.length > 5) {
            return expiryDate.substring(0, EXPIRY_DATE_DEFAULTS.validLengths.max()!!)
        }
        return expiryDate
    }

}
