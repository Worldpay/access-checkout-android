package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand

@Deprecated(message = "legacy")
typealias CVV = String
@Deprecated(message = "legacy")
typealias Month = String
@Deprecated(message = "legacy")
typealias Year = String

/**
 * The interface responsible for validating the state of card fields
 */
@Deprecated(message = "legacy")
interface CardValidator {

    /**
     * The (optional) [CardConfiguration] to use for validating the fields
     */
    val cardConfiguration: CardConfiguration?

    /**
     * Validates the pan field
     *
     * @param pan the pan to validate
     * @return a [Pair] of [ValidationResult] and [RemoteCardBrand] for the pan field
     */
    fun validatePAN(pan: String): Pair<ValidationResult, RemoteCardBrand?>

    /**
     * Validates the cvv field
     *
     * @param cvv the pan to validate
     * @param pan (Optional) the pan field to validate against the cvv
     * @return a [Pair] of [ValidationResult] and [RemoteCardBrand] for the cvv field
     */
    fun validateCVV(cvv: CVV, pan: String?): Pair<ValidationResult, RemoteCardBrand?>

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
