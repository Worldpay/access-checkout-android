package com.worldpay.access.checkout.session.request.handlers

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