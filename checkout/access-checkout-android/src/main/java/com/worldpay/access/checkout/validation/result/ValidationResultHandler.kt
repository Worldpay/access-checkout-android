package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.client.validation.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.validation.ValidationResult
import java.util.concurrent.atomic.AtomicBoolean

class ValidationResultHandler(
    private val validationListener: AccessCheckoutCardValidationListener
) {

    private var panValidated = AtomicBoolean(false)
    private var monthValidated = AtomicBoolean(false)
    private var yearValidated = AtomicBoolean(false)
    private var cvvValidated = AtomicBoolean(false)

    fun handlePanValidationResult(validationResult: ValidationResult, cardBrand: CardBrand?) {
        validationListener.onPanValidated(cardBrand, validationResult.complete)
        panValidated.set(validationResult.complete)
        checkAllFields()
    }

    fun handleExpiryMonthValidationResult(validationResult: ValidationResult) {
        validationListener.onExpiryDateValidated(validationResult.complete)
        monthValidated.set(validationResult.complete)
        checkAllFields()
    }

    fun handleExpiryYearValidationResult(validationResult: ValidationResult) {
        validationListener.onExpiryDateValidated(validationResult.complete)
        yearValidated.set(validationResult.complete)
        checkAllFields()
    }

    fun handleCvvValidationResult(validationResult: ValidationResult) {
        validationListener.onCvvValidated(validationResult.complete)
        cvvValidated.set(validationResult.complete)
        checkAllFields()
    }

    private fun checkAllFields() {
        if (allDetailsValidated()) {
            validationListener.onValidationSuccess()
        }
    }

    private fun allDetailsValidated() = panValidated.get() && monthValidated.get() && yearValidated.get() && cvvValidated.get()

}