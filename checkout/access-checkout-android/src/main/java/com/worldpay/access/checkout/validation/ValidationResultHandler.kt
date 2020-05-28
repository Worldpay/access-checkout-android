package com.worldpay.access.checkout.validation

import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.client.AccessCheckoutValidationListener
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
import java.util.concurrent.atomic.AtomicBoolean

class ValidationResultHandler(
    private val validationListener: AccessCheckoutValidationListener,
    private val cardDetailComponents: CardDetailComponents
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

    private fun getInvalidFields(): Map<CardDetailType, EditText> {
        val fields = mutableMapOf<CardDetailType, EditText>()
        if (!panValidated.get()) {
            fields[PAN] = cardDetailComponents.pan
        }
        if (!monthValidated.get()) {
            fields[EXPIRY_MONTH] = cardDetailComponents.expiryMonth
        }
        if (!yearValidated.get()) {
            fields[EXPIRY_YEAR] = cardDetailComponents.expiryYear
        }
        if (!cvvValidated.get()) {
            fields[CVV] = cardDetailComponents.cvv
        }

        return fields
    }

}