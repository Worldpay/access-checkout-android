package com.worldpay.access.checkout.session.api.request

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.session.api.client.SessionClient
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo

/**
 * A factory for creating a [RequestDispatcher]
 */
internal class RequestDispatcherFactory {

    /**
     * Creates an returns a [RequestDispatcher]
     *
     * @param[path] the URL path for the API
     * @param[sessionClient] the [SessionClient] implementation for the requested session
     * @param[sessionResponseCallback] the [Callback] with [SessionResponseInfo] generic type where the response will be returned
     *
     * @return [RequestDispatcher] that will dispatch the request for a session
     */
    fun getInstance(
        path: String,
        sessionClient: SessionClient,
        sessionResponseCallback: Callback<SessionResponseInfo>
    ): RequestDispatcher {
        return RequestDispatcher(path, sessionResponseCallback, sessionClient)
    }

}
