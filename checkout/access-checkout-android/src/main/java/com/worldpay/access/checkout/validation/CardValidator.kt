package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration

typealias PAN = String
typealias CVV = String
typealias Month = String
typealias Year = String

/**
 * The interface responsible for validating the state of card fields
 */
interface CardValidator {

    /**
     * The (optional) [CardConfiguration] to use for validating the fields
     */
    var cardConfiguration: CardConfiguration?

    /**
     * Validates the pan field
     *
     * @param pan the pan to validate
     * @return a [Pair] of [ValidationResult] and [CardBrand] for the pan field
     */
    fun validatePAN(pan: PAN): Pair<ValidationResult, CardBrand?>

    /**
     * Validates the cvv field
     *
     * @param cvv the pan to validate
     * @param pan (Optional) the pan field to validate against the cvv
     * @return a [Pair] of [ValidationResult] and [CardBrand] for the cvv field
     */
    fun validateCVV(cvv: CVV, pan: PAN?): Pair<ValidationResult, CardBrand?>

    /**
     * Validates the date field
     *
     * @param month (Optional) the month to validate
     * @param year (Optional) the year to validate
     * @return a [ValidationResult] for the date field
     */
    fun validateDate(month: Month?, year: Year?): ValidationResult

    /**
     * Determines whether the date field can be updated with extra characters
     *
     * @param month (Optional) the month to validate
     * @param year (Optional) the year to validate
     * @return true if extra characters can be entered, false otherwise
     */
    fun canUpdate(month: Month?, year: Year?): Boolean
}
