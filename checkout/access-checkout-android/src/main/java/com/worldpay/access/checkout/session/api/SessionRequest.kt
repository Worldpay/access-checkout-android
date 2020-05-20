package com.worldpay.access.checkout.session.api

import java.io.Serializable

internal interface SessionRequest: Serializable

internal data class CardSessionRequest(val cardNumber: String, val cardExpiryDate: CardExpiryDate, val cvv: String, val identity: String):
    SessionRequest {

    internal data class CardExpiryDate(val month: Int, val year: Int): Serializable

}

internal data class CVVSessionRequest(val cvv: String, val identity: String):
    SessionRequest
