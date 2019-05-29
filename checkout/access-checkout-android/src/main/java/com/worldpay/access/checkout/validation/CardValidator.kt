package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand

typealias PAN = String
typealias CVV = String
typealias Month = String
typealias Year = String

interface CardValidator {

    fun validatePAN(pan: PAN): Pair<ValidationResult, CardBrand?>

    fun validateCVV(cvv: CVV, pan: PAN?): Pair<ValidationResult, CardBrand?>

    fun validateDate(month: Month?, year: Year?): ValidationResult

    fun canUpdate(month: Month?, year: Year?): Boolean
}