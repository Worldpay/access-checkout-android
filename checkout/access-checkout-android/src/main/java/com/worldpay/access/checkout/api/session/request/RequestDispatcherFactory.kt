package com.worldpay.access.checkout.api.session.request

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.session.SessionResponseInfo
import com.worldpay.access.checkout.api.session.client.SessionClient

internal class RequestDispatcherFactory {

    fun getInstance(
        path: String,
        sessionClient: SessionClient,
        sessionResponseCallback: Callback<SessionResponseInfo>
    ): RequestDispatcher {
        return RequestDispatcher(path, sessionResponseCallback, sessionClient)
    }

}
