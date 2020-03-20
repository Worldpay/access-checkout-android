package com.worldpay.access.checkout.api

import java.io.Serializable

internal data class CardSessionRequest(val cardNumber: String, val cardExpiryDate: CardExpiryDate, val cvv: String, val identity: String): Serializable {

    internal data class CardExpiryDate(val month: Int, val year: Int): Serializable
}