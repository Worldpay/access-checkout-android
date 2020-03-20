package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.api.Callback

internal class RequestDispatcherFactory {

    fun getInstance(path: String, sessionResponseCallback: Callback<SessionResponse>): RequestDispatcher {
        return RequestDispatcher.buildDispatcher(path, sessionResponseCallback)
    }

}
