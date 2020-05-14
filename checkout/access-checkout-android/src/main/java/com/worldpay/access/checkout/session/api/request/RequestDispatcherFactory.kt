package com.worldpay.access.checkout.session.api.request

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.session.api.SessionResponseInfo
import com.worldpay.access.checkout.session.api.client.SessionClient

internal class RequestDispatcherFactory {

    fun getInstance(
        path: String,
        sessionClient: SessionClient,
        sessionResponseCallback: Callback<SessionResponseInfo>
    ): RequestDispatcher {
        return RequestDispatcher(path, sessionResponseCallback, sessionClient)
    }

}
