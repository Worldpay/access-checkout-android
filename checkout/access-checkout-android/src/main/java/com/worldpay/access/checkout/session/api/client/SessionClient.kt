package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.session.api.SessionRequest
import com.worldpay.access.checkout.session.api.SessionResponse
import java.net.URL

internal interface SessionClient {
    fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse?
}

