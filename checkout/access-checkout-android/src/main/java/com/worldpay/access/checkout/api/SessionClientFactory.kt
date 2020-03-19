package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.serialization.SessionRequestSerializer
import com.worldpay.access.checkout.api.serialization.SessionResponseDeserializer

class SessionClientFactory {

    internal fun createClient(): SessionClient {
        return SessionClientImpl(SessionResponseDeserializer(), SessionRequestSerializer(), HttpClient())
    }

}