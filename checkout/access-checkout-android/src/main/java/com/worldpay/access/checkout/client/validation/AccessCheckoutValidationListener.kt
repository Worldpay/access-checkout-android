package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.api.configuration.CardBrand

interface AccessCheckoutValidationListener

interface AccessCheckoutValidationSuccessListener: AccessCheckoutValidationListener {
    fun onValidationSuccess()
}

interface AccessCheckoutCvvValidationListener: AccessCheckoutValidationSuccessListener {
    fun onCvvValidated(isValid: Boolean)
}

interface AccessCheckoutPanValidationListener: AccessCheckoutValidationSuccessListener {
    fun onPanValidated(cardBrand: CardBrand?, isValid: Boolean)
}

interface AccessCheckoutExpiryDateValidationListener: AccessCheckoutValidationSuccessListener {
    fun onExpiryDateValidated(isValid: Boolean)
}

interface AccessCheckoutCardValidationListener:
    AccessCheckoutCvvValidationListener,
    AccessCheckoutPanValidationListener,
    AccessCheckoutExpiryDateValidationListener
