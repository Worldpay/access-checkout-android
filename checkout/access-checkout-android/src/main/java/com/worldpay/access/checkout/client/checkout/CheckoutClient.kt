package com.worldpay.access.checkout.client.checkout

import com.worldpay.access.checkout.client.card.CardDetails

interface CheckoutClient {

    fun generateSessionState(cardDetails: CardDetails)

}
