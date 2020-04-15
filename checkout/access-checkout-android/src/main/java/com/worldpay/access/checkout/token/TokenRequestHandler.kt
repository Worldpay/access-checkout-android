package com.worldpay.access.checkout.token

import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.token.TokenRequest

interface TokenRequestHandler {

    fun canHandle(tokenRequests: List<TokenRequest>): Boolean

    fun handle(cardDetails: CardDetails)

}
