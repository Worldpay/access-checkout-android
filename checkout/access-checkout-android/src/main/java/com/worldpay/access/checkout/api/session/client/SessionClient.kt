package com.worldpay.access.checkout.api.session.client

import com.worldpay.access.checkout.api.session.SessionRequest
import com.worldpay.access.checkout.api.session.SessionResponse
import java.net.URL

internal interface SessionClient {
    fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse?
}

