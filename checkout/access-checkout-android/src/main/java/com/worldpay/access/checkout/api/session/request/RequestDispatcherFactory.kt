package com.worldpay.access.checkout.api.session.request

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.api.session.client.SessionClient

internal class RequestDispatcherFactory {

    fun getInstance(
        path: String,
        sessionClient: SessionClient,
        sessionResponseCallback: Callback<SessionResponse>
    ): RequestDispatcher {
        return RequestDispatcher(path, sessionResponseCallback, sessionClient)
    }

}
