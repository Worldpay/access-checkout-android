package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardBrand

interface AccessCheckoutValidationListener {

    fun onPanValidated(cardBrand: CardBrand?, isValid: Boolean)

    fun onExpiryDateValidated(isValid: Boolean)

    fun onCvvValidated(cardBrand: CardBrand?, isValid: Boolean)

    fun onValidationSuccess()

}

interface AccessCheckoutCvvValidationListener {

    fun onCvvValidated(isValid: Boolean)

    fun onValidationSuccess()

}

interface AccessCheckoutCardValidationListener {

    fun onPanValidated(cardBrand: CardBrand?, isValid: Boolean)

    fun onExpiryDateValidated(isValid: Boolean)

    fun onCvvValidated(isValid: Boolean)

    fun onValidationSuccess()

}