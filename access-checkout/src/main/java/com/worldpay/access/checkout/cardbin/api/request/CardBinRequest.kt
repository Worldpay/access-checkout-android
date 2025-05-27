package com.worldpay.access.checkout.cardbin.api.request

import java.io.Serializable

/**
 * A serializable type interface which specific request implementations represent
 */
internal interface CardBinRequest : Serializable

/**
 * A serializable class that represents the request body for the card bin api endpoint
 *
 * @property [cardNumber] A [String] representing the card number
 * @property [checkoutId] A [String] representing the checkout id
 */
internal data class CardBinApiRequest(
    val cardNumber: String,
    val checkoutId: String
) :
    CardBinRequest