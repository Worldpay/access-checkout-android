package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardValidationRule
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor
import java.util.*
import java.util.Calendar.*

interface DateValidator {
    fun validate(month: Month?, year: Year?): ValidationResult
    fun canUpdate(month: Month?, year: Year?): Boolean
}

internal class DateValidatorImpl(
    private val now: Calendar,
    private val cardConfiguration: CardConfiguration
) : DateValidator {

    private val defaultValidationResult = ValidationResult(partial = true, complete = true)

    override fun validate(month: Month?, year: Year?): ValidationResult {
        val defaults = cardConfiguration.defaults ?: return defaultValidationResult
        val monthRule = defaults.month ?: return defaultValidationResult
        val yearRule = defaults.year ?: return defaultValidationResult

        var partial = true
        var complete = true

        month?.let {
            val validationResultForMonth = if (it == "0" || it == "1") {
                ValidationResult(partial = true, complete = false)
            } else {
                getValidationResult(monthRule, it)
            }

            partial = validationResultForMonth.partial
            complete = validationResultForMonth.complete

            if (!partial && !complete) {
                return ValidationResult(partial = false, complete = false)
            }
        }

        year?.let {
            val validationResultForYear = getValidationResult(yearRule, it)
            var validDate = isYearValid(it, validationResultForYear)

            if (month != null && validDate) {
                validDate = isFullDateValid(it, month)
            }

            partial = partial && validationResultForYear.partial
            complete = complete && validDate
        }

        return ValidationResult(partial, complete)
    }

    override fun canUpdate(month: Month?, year: Year?): Boolean {
        val defaults = cardConfiguration.defaults ?: return true
        val monthRule = defaults.month ?: return true
        val yearRule = defaults.year ?: return true

        var complete: Boolean

        complete = month?.let {
            val validationResult = getValidationResultFor(it, monthRule)
            validationResult.complete
        } ?: false

        complete = year?.let {
            val validationResult = getValidationResultFor(it, yearRule)
            complete && validationResult.complete
        } ?: false

        return !complete
    }

    private fun getValidationResult(rule: CardValidationRule, insertedDateField: String): ValidationResult {
        if (rule.matcher != null) {
            return if (ValidatorUtils.regexMatches(rule.matcher, insertedDateField)) {
                getValidationResultFor(insertedDateField, rule)
            } else {
                ValidationResult(partial = false, complete = false)
            }
        }

        return getValidationResultFor(insertedDateField, rule)
    }

    private fun isFullDateValid(year: Year, month: Month): Boolean {
        val validityCal = getInstance()
        validityCal.set(
            Integer.parseInt("${getYearPrefix(now)}$year"),
            Integer.parseInt(month) - 1,
            1,
            validityCal.getActualMaximum(HOUR_OF_DAY),
            validityCal.getActualMaximum(MINUTE),
            validityCal.getActualMaximum(SECOND)
        )
        validityCal.set(MILLISECOND, validityCal.getActualMaximum(MILLISECOND))
        validityCal.set(DATE, validityCal.getActualMaximum(DATE))

        return validityCal.timeInMillis >= now.timeInMillis
    }

    private fun isYearValid(year: Year, validationResult: ValidationResult): Boolean =
        validationResult.complete
                && yearIsPresentOrFuture(year)

    private fun yearIsPresentOrFuture(insertedYear: Year): Boolean {
        val inflatedInsertedYear =
            Integer.parseInt(inflateYear(now, insertedYear))

        return inflatedInsertedYear >= getYear(now)
    }

    private fun getYearPrefix(calendar: Calendar) = getYear(calendar).toString().substring(0, 2)

    private fun inflateYear(calendar: Calendar, year: Year) = getYearPrefix(calendar) + year

    private fun getYear(calendar: Calendar) = calendar.get(YEAR)
}
