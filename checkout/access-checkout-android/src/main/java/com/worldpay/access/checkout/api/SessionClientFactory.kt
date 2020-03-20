package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.api.serialization.SessionResponseDeserializer

class SessionClientFactory {

    internal fun createClient(): SessionClient {
        return SessionClientImpl(SessionResponseDeserializer(), CardSessionRequestSerializer(), HttpClient())
    }

}