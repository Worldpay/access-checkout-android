package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.client.validation.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.validation.card.CardDetailType
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
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
        } else {
            validationListener.onValidationFailure(getInvalidFields())
        }
    }

    private fun allDetailsValidated() = panValidated.get() && monthValidated.get() && yearValidated.get() && cvvValidated.get()

    private fun getInvalidFields(): List<CardDetailType> {
        val fields = mutableListOf<CardDetailType>()
        if (!panValidated.get()) {
            fields.add(PAN)
        }
        if (!monthValidated.get()) {
            fields.add(EXPIRY_MONTH)
        }
        if (!yearValidated.get()) {
            fields.add(EXPIRY_YEAR)
        }
        if (!cvvValidated.get()) {
            fields.add(CVV)
        }

        return fields
    }

}