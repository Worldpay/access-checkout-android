package com.worldpay.access.checkout.session.handlers

/**
 * This class is responsible for creating all the [SessionRequestHandler] implementations
 *
 * @property sessionRequestHandlerConfig The [SessionRequestHandlerConfig] that should be used in the handler implementations
 */
internal class SessionRequestHandlerFactory(sessionRequestHandlerConfig: SessionRequestHandlerConfig) {

    private val handlers = listOf(
        CardSessionRequestHandler(
            sessionRequestHandlerConfig
        ),
        CvcSessionRequestHandler(
            sessionRequestHandlerConfig
        )
    )

    fun getTokenHandlers(): List<SessionRequestHandler> {
        return handlers
    }

}
