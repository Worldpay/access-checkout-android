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
 * [SessionClientFactory] returns the correct implementation of [SessionClient] based on the type of [SessionRequest]
 */

internal class SessionClientFactory {

    /**
     * Method that returns a [SessionClient]
     *
     * @param sessionRequest - the session request information
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