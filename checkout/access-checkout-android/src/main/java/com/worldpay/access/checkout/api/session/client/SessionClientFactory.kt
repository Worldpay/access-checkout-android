package com.worldpay.access.checkout.api.session.client

import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequest
import com.worldpay.access.checkout.api.session.serialization.CVVSessionRequestSerializer
import com.worldpay.access.checkout.api.session.serialization.CVVSessionResponseDeserializer
import com.worldpay.access.checkout.api.session.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.api.session.serialization.CardSessionResponseDeserializer

internal class SessionClientFactory {

    fun createClient(sessionRequest: SessionRequest): SessionClient {
        when (sessionRequest) {
            is CardSessionRequest -> {
                return CardSessionClient(
                    CardSessionResponseDeserializer(),
                    CardSessionRequestSerializer(),
                    HttpClient()
                )
            }
            is CVVSessionRequest -> {
                return CVVSessionClient(
                    CVVSessionResponseDeserializer(),
                    CVVSessionRequestSerializer(),
                    HttpClient()
                )
            }
            else -> {
                throw IllegalArgumentException("unknown session request type found")
            }
        }
    }

}