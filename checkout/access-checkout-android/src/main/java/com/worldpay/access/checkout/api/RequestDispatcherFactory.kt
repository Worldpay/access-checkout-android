package com.worldpay.access.checkout.api

internal class RequestDispatcherFactory {
    fun getInstance(
        path: String,
        sessionResponseCallback: Callback<SessionResponse>
    ): RequestDispatcher =
        RequestDispatcher.buildDispatcher(path, sessionResponseCallback)

}
