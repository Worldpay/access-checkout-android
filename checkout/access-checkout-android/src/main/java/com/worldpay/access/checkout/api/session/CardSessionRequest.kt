package com.worldpay.access.checkout.api.session

import java.io.Serializable

interface SessionRequest: Serializable

internal data class CardSessionRequest(val cardNumber: String, val cardExpiryDate: CardExpiryDate, val cvv: String, val identity: String): SessionRequest {

    internal data class CardExpiryDate(val month: Int, val year: Int): Serializable

}