package com.worldpay.access.checkout.client.validation.listener

import com.worldpay.access.checkout.client.validation.model.CardBrand

/**
 * Type interface that allows polymorphism of different types of validation listeners
 */
interface AccessCheckoutValidationListener

/**
 * Interface that should be used when validation is successful
 */
interface AccessCheckoutValidationSuccessListener : AccessCheckoutValidationListener {
    fun onValidationSuccess()
}

/**
 * Interface that should be used when cvc validation state changes
 */
interface AccessCheckoutCvcValidationListener : AccessCheckoutValidationSuccessListener {
    fun onCvcValidated(isValid: Boolean)
}

/**
 * Interface that should be used when pan validation state changes
 */
interface AccessCheckoutPanValidationListener : AccessCheckoutValidationSuccessListener {
    fun onPanValidated(isValid: Boolean)
}

/**
 * Interface that should be used when card brand changes
 */
interface AccessCheckoutBrandsChangedListener {
    fun onCardBrandsChanged(cardBrands: List<CardBrand>)
}

/**
 * Interface that should be used when expiry date validation state changes
 */
interface AccessCheckoutExpiryDateValidationListener : AccessCheckoutValidationSuccessListener {
    fun onExpiryDateValidated(isValid: Boolean)
}

/**
 * Interface that should be used by clients to amalgamate all relevant listeners for the card details
 */
interface AccessCheckoutCardValidationListener :
    AccessCheckoutCvcValidationListener,
    AccessCheckoutPanValidationListener,
    AccessCheckoutExpiryDateValidationListener,
    AccessCheckoutBrandsChangedListener
