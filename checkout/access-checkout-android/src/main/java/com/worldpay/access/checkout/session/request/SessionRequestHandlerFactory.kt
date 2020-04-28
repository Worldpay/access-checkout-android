package com.worldpay.access.checkout.session.request

import com.worldpay.access.checkout.session.request.handlers.PaymentsCvcSessionRequestHandler
import com.worldpay.access.checkout.session.request.handlers.VerifiedTokensSessionRequestHandler

internal class SessionRequestHandlerFactory(sessionRequestHandlerConfig: SessionRequestHandlerConfig) {

    private val handlers = listOf(
        VerifiedTokensSessionRequestHandler(
            sessionRequestHandlerConfig
        ),
        PaymentsCvcSessionRequestHandler(
            sessionRequestHandlerConfig
        )
    )

    fun getTokenHandlers(): List<SessionRequestHandler> {
        return handlers
    }

}