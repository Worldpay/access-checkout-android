package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.validation.card.CardDetailType

interface AccessCheckoutValidationListener {

    fun onPanValidated(cardBrand: CardBrand?, isValid: Boolean)

    fun onExpiryDateValidated(isValid: Boolean)

    fun onCvvValidated(cardBrand: CardBrand?, isValid: Boolean)

    fun onValidationSuccess()

    fun onValidationFailure(invalidFields: Map<CardDetailType, EditText>)

}

interface AccessCheckoutCvvValidationListener {

    fun onCvvValidated(cardBrand: CardBrand?, isValid: Boolean)

    fun onValidationSuccess()

    fun onValidationFailure(invalidFields: List<CardDetailType>)

}

interface AccessCheckoutCardValidationListener {

    fun onPanValidated(cardBrand: CardBrand?, isValid: Boolean)

    fun onExpiryDateValidated(isValid: Boolean)

    fun onCvvValidated(isValid: Boolean)

    fun onValidationSuccess()

    fun onValidationFailure(invalidFields: List<CardDetailType>)

}