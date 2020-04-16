package com.worldpay.access.checkout.client

interface AccessCheckoutClient {

    fun generateSession(cardDetails: CardDetails, sessionTypes: List<SessionType>)

}
