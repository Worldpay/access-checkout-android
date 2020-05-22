package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.MONTH_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.YEAR_DEFAULTS
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor
import java.util.*
import java.util.Calendar.*

/**
 * Validator for the date field
 */
interface DateValidator {
    /**
     * Validates the date field
     *
     * @param month (Optional) the month to validate
     * @param year (Optional) the year to validate
     * @return a [ValidationResult] for the date field
     */
    fun validate(month: Month?, year: Year?): ValidationResult

    /**
     * Determines whether the date field can be updated with extra characters
     *
     * @param month (Optional) the month to validate
     * @param year (Optional) the year to validate
     * @return true if extra characters can be entered, false otherwise
     */
    fun canUpdate(month: Month?, year: Year?): Boolean
}

internal class DateValidatorImpl(
    private val now: Calendar,
    private val cardConfiguration: CardConfiguration?
) : DateValidator {

    override fun validate(month: Month?, year: Year?): ValidationResult {
        val (monthRule, yearRule) = getRulesForDate(cardConfiguration)

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

            if (!month.isNullOrBlank() && validDate) {
                validDate = isFullDateValid(it, month)
            }

            partial = partial && validationResultForYear.partial
            complete = complete && validDate
        }

        return ValidationResult(partial, complete)
    }

    override fun canUpdate(month: Month?, year: Year?): Boolean {
        val (monthRule, yearRule) = getRulesForDate(cardConfiguration)

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

    private fun getRulesForDate(cardConfiguration: CardConfiguration?): Pair<CardValidationRule, CardValidationRule> {
        if (cardConfiguration == null) {
            return Pair(MONTH_DEFAULTS, YEAR_DEFAULTS)
        }
        return Pair(cardConfiguration.defaults.month, cardConfiguration.defaults.year)
    }

    private fun getValidationResult(rule: CardValidationRule, insertedDateField: String): ValidationResult {
        return when {
            ValidatorUtils.regexMatches(rule.matcher, insertedDateField) -> getValidationResultFor(insertedDateField, rule)
            insertedDateField.isBlank() -> ValidationResult(partial = true, complete = false)
            else -> ValidationResult(partial = false, complete = false)
        }
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
