package com.worldpay.access.checkout.session.api.request

import com.worldpay.access.checkout.session.api.request.CardSessionRequest.CardExpiryDate
import java.io.Serializable

/**
 * A serializable type interface which specific request implementations represent
 */
internal interface SessionRequest : Serializable

/**
 * A serializable class that represents the request body for the verified token session endpoint
 *
 * @property [cardNumber] A [String] representing the card number
 * @property [cardExpiryDate] A [CardExpiryDate] representing the card expiry
 * @property [cvc] A [String] representing the card's cvc number
 * @property [identity] A [String] representing the merchant id
 */
internal data class CardSessionRequest(
    val cardNumber: String,
    val cardExpiryDate: CardExpiryDate,
    val cvc: String,
    val identity: String
) :
    SessionRequest {

    /**
     * This class represents an expiry date for a [CardExpiryDate]
     *
     * @property [month] the expiry month
     * @property [year] the expiry year
     */
    internal data class CardExpiryDate(val month: Int, val year: Int) : Serializable
}

/**
 * A serializable class that represents the request body for the Payments CVC session endpoint
 *
 * @property [cvc] A [String] representing the card's cvc number
 * @property [identity] A [String] representing the merchant id
 */
internal data class CvcSessionRequest(val cvc: String, val identity: String) : SessionRequest
