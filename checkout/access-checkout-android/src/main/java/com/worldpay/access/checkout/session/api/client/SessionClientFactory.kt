package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.session.api.request.CVVSessionRequest
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.serialization.CVVSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CVVSessionResponseDeserializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionResponseDeserializer

/**
 * [SessionClientFactory] is used to create instances of [SessionClient]
 */

internal class SessionClientFactory {

    /**
     * @param[sessionRequest] - the session request information
     * @return an implementation of [SessionClient]
     */
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