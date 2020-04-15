package com.worldpay.access.checkout.client.checkout

import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.token.TokenRequest

interface CheckoutClient {

    fun generateSession(cardDetails: CardDetails, tokenRequests: List<TokenRequest>)

}
