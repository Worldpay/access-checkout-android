package com.worldpay.access.checkout.session.handlers

import android.content.Intent

/**
 * A factory class that is initialised with a [SessionRequestHandlerConfig]
 * @property getTokenHandlers() returns a list of [SessionRequestHandler]s
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