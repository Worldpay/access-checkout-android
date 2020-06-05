package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardBrand

interface AccessCheckoutValidationListener

interface AccessCheckoutValidationSuccessListener: AccessCheckoutValidationListener {
    fun onValidationSuccess()
}

interface AccessCheckoutCvvValidatedSuccessListener: AccessCheckoutValidationSuccessListener {
    fun onCvvValidated(isValid: Boolean)
}

interface AccessCheckoutPanValidatedSuccessListener: AccessCheckoutValidationSuccessListener {
    fun onPanValidated(cardBrand: CardBrand?, isValid: Boolean)
}

interface AccessCheckoutExpiryDateValidatedSuccessListener: AccessCheckoutValidationSuccessListener {
    fun onExpiryDateValidated(isValid: Boolean)
}

interface AccessCheckoutCardValidationSuccessListener:
    AccessCheckoutCvvValidatedSuccessListener,
    AccessCheckoutPanValidatedSuccessListener,
    AccessCheckoutExpiryDateValidatedSuccessListener