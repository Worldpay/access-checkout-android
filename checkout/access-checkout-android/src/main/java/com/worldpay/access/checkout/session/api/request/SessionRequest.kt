package com.worldpay.access.checkout.session.api.request

import java.io.Serializable

/**
 * [SessionRequest] is a serializable interface to which specific implementations for a given service adhere.
 */
internal interface SessionRequest: Serializable

/**
 * [CardSessionRequest] is a serializable class that contains the request data for a VT session
 */
internal data class CardSessionRequest(val cardNumber: String, val cardExpiryDate: CardExpiryDate, val cvv: String, val identity: String):
    SessionRequest {

    internal data class CardExpiryDate(val month: Int, val year: Int): Serializable

}

/**
 * [CVVSessionRequest] is a serializable class that contains the request data for a Payments CVC session
 */
internal data class CVVSessionRequest(val cvv: String, val identity: String):
    SessionRequest
