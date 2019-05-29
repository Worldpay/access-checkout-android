package com.worldpay.access.checkout

import android.text.InputFilter
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.validation.ValidationResult

interface CardListener {
    fun onPANUpdateValidationResult(validationResult: ValidationResult, cardBrand: CardBrand?, lengthFilter: InputFilter?)
    fun onPANEndUpdateValidationResult(validationResult: ValidationResult, cardBrand: CardBrand?)

    fun onCVVUpdateValidationResult(validationResult: ValidationResult, lengthFilter: InputFilter?)
    fun onCVVEndUpdateValidationResult(validationResult: ValidationResult)

    fun onDateUpdateValidationResult(monthValidationResult: ValidationResult?, yearValidationResult: ValidationResult?,
                                     monthLengthFilter: InputFilter, yearLengthFilter: InputFilter)
    fun onDateEndUpdateValidationResult(validationResult: ValidationResult)

    fun onValidationResult(validationResult: ValidationResult)
}