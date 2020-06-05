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

    fun handle(cardDetailType: CardDetailType, validationResult: ValidationResult, cardBrand: CardBrand? = null) {
        if (cardDetailType == PAN) {
            validationListener.onPanValidated(cardBrand, validationResult.complete)
            panValidated.set(validationResult.complete)
        }

        if (cardDetailType == EXPIRY_MONTH) {
            validationListener.onExpiryDateValidated(validationResult.complete)
            monthValidated.set(validationResult.complete)
        }

        if (cardDetailType == EXPIRY_YEAR) {
            validationListener.onExpiryDateValidated(validationResult.complete)
            yearValidated.set(validationResult.complete)
        }

        if (cardDetailType == CVV) {
            validationListener.onCvvValidated(cardBrand, validationResult.complete)
            cvvValidated.set(validationResult.complete)
        }

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