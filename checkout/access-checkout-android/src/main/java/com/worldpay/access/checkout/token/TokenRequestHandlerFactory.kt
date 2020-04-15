package com.worldpay.access.checkout.token

import com.worldpay.access.checkout.token.handlers.SessionTokenRequestRequestHandler
import com.worldpay.access.checkout.token.handlers.VerifiedTokenRequestRequestHandler

internal class TokenRequestHandlerFactory(private val tokenRequestHandlerConfig: TokenRequestHandlerConfig) {

    private val handlers = listOf(
        VerifiedTokenRequestRequestHandler(tokenRequestHandlerConfig),
        SessionTokenRequestRequestHandler(tokenRequestHandlerConfig)
    )

    fun getTokenHandlers(): List<TokenRequestHandler> {
        return handlers
    }

}