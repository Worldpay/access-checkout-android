package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.session.api.CVVSessionRequest
import com.worldpay.access.checkout.session.api.CardSessionRequest
import com.worldpay.access.checkout.session.api.SessionRequest
import com.worldpay.access.checkout.session.api.serialization.CVVSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CVVSessionResponseDeserializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionResponseDeserializer

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