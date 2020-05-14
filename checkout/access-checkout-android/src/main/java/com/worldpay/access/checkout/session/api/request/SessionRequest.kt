package com.worldpay.access.checkout.session.api.request

import java.io.Serializable

/**
 * [SessionRequest] is a serializable interface to which specific implementations for a given service adhere.
 */
internal interface SessionRequest: Serializable

/**
 * [CardSessionRequest] is a serializable class that represents the request body for the VT session endpoint
 */
internal data class CardSessionRequest(val cardNumber: String, val cardExpiryDate: CardExpiryDate, val cvv: String, val identity: String):
    SessionRequest {

    internal data class CardExpiryDate(val month: Int, val year: Int): Serializable

}

/**
 * [CVVSessionRequest] is a serializable class that represents the request body for the Payments CVC session endpoint
 */
internal data class CVVSessionRequest(val cvv: String, val identity: String):
    SessionRequest
