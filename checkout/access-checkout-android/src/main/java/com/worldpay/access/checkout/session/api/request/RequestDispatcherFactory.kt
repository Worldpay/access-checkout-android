package com.worldpay.access.checkout.session.api.request

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.session.api.client.SessionClient
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo

internal class RequestDispatcherFactory {

    fun getInstance(
        path: String,
        sessionClient: SessionClient,
        sessionResponseCallback: Callback<SessionResponseInfo>
    ): RequestDispatcher {
        return RequestDispatcher(path, sessionResponseCallback, sessionClient)
    }

}
