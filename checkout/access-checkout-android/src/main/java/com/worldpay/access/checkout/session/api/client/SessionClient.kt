package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import java.net.URL

internal interface SessionClient {
    fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse?
}

