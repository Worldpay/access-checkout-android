package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.DefaultCardRules
import com.worldpay.access.checkout.validation.Month
import com.worldpay.access.checkout.validation.NewValidatorUtils.getValidationResultFor
import com.worldpay.access.checkout.validation.Year
import java.util.*

class NewDateValidator(private val now: Calendar = Calendar.getInstance()) {
    private val monthRule = DefaultCardRules.MONTH_DEFAULTS
    private val yearRule = DefaultCardRules.YEAR_DEFAULTS

    fun validate(month: String?, year: String?) : Boolean {
        if (month.isNullOrBlank() || year.isNullOrBlank()) {
            return false
        }

        val monthResult = getValidationResultFor(month, monthRule)
        val yearResult = getValidationResultFor(year, yearRule)

        if (!yearResult || !monthResult ) {
            return false
        }

        return isFullDateValid(year, month)
    }

    private fun isFullDateValid(year: Year, month: Month): Boolean {
        val validityCal = Calendar.getInstance()
        validityCal.set(
            Integer.parseInt("${getYearPrefix(now)}$year"),
            Integer.parseInt(month),
            1,
            validityCal.getActualMaximum(Calendar.HOUR_OF_DAY),
            validityCal.getActualMaximum(Calendar.MINUTE),
            validityCal.getActualMaximum(Calendar.SECOND)
        )
        validityCal.set(Calendar.MILLISECOND, validityCal.getActualMaximum(Calendar.MILLISECOND))
        validityCal.set(Calendar.DATE, validityCal.getActualMaximum(Calendar.DATE))

        return validityCal.timeInMillis >= now.timeInMillis
    }

    private fun getYearPrefix(calendar: Calendar) = getYear(calendar).toString().substring(0, 2)

    private fun getYear(calendar: Calendar) = calendar.get(Calendar.YEAR)

}