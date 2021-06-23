package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import java.lang.Integer.parseInt
import java.util.Calendar

internal class ExpiryDateValidator(private val now: Calendar = Calendar.getInstance()) {

    private val expiryDateRule = DefaultCardRules.EXPIRY_DATE_DEFAULTS

    private val simpleValidator = SimpleValidator()

    fun validate(expiryDate: String): Boolean {
        if (!expiryDate.contains("/")) {
            return false
        }

        val isValid = simpleValidator.validate(expiryDate, expiryDateRule)

        if (isValid) {
            val month = parseInt(expiryDate.split("/")[0])
            val year = parseInt(expiryDate.split("/")[1])
            return isExpiryDateValid(month, year)
        }

        return false
    }

    private fun isExpiryDateValid(month: Int, year: Int): Boolean {
        val expiryDate = toCalendarInstance(month, year)
        return expiryDate.timeInMillis >= now.timeInMillis
    }

    private fun toCalendarInstance(month: Int, year: Int): Calendar {
        val cal = Calendar.getInstance()

        cal.set(Calendar.YEAR, parseInt("${getYearPrefix(now)}$year"))
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))

        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE))
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND))
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND))

        return cal
    }

    private fun getYearPrefix(calendar: Calendar) = getYear(calendar).toString().substring(0, 2)

    private fun getYear(calendar: Calendar) = calendar.get(Calendar.YEAR)
}
