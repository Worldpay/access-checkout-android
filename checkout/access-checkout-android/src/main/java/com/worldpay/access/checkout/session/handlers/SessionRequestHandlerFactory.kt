package com.worldpay.access.checkout.session.handlers

import android.content.Intent

/**
 * [SessionRequestHandlerFactory] is responsible for creating all the [SessionRequestHandler]
 */
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