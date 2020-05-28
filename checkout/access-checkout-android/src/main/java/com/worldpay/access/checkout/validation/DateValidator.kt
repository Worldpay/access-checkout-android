package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.ValidatorUtils.getValidationResultFor
import java.util.*
import java.util.Calendar.*

class DateValidator(private val now: Calendar = getInstance()) {

    fun validate(month: Month?, year: Year?, cardConfiguration: CardConfiguration): ValidationResult {
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

    fun canUpdate(month: Month?, year: Year?, cardConfiguration: CardConfiguration): Boolean {
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

    private fun getRulesForDate(cardConfiguration: CardConfiguration): Pair<CardValidationRule, CardValidationRule> {
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
