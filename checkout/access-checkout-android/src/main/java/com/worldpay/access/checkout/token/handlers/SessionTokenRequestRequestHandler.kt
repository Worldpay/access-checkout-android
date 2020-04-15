package com.worldpay.access.checkout.token.handlers

import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.token.TokenRequest
import com.worldpay.access.checkout.client.token.TokenRequest.SESSION_TOKEN
import com.worldpay.access.checkout.token.TokenRequestHandler

class SessionTokenRequestRequestHandler: TokenRequestHandler {

    override fun canHandle(tokenRequests: List<TokenRequest>): Boolean {
        return tokenRequests.contains(SESSION_TOKEN)
    }

    override fun handle(cardDetails: CardDetails) {
        TODO("Not yet implemented")
    }

}