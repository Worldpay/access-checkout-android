package com.worldpay.access.checkout.validation.cardbin.api

import java.io.Serializable

/**
 * A serializable class that represents the request body for the card bin api endpoint
 *
 * @property [cardNumber] A [String] representing the card number
 * @property [checkoutId] A [String] representing the checkout id
 */
internal data class CardBinRequest(
    val cardNumber: String,
    val checkoutId: String
) : Serializable
