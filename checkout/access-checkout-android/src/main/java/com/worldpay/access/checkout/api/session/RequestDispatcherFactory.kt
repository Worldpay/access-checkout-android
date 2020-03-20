package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.SessionResponse

internal class RequestDispatcherFactory {
    fun getInstance(
        path: String,
        sessionResponseCallback: Callback<SessionResponse>
    ): RequestDispatcher =
        RequestDispatcher.buildDispatcher(path, sessionResponseCallback)

}
