package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionResponseDeserializer
import com.worldpay.access.checkout.session.api.serialization.CvcSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CvcSessionResponseDeserializer

/**
 * Factory class that is used to create instances of [SessionClient]
 */
internal class SessionClientFactory {

    /**
     * Creates a new instance of a [SessionClient] implementation depending on the type of the
     * [SessionRequest]
     *
     * @param[sessionRequest] The session request information
     *
     * @return an implementation of [SessionClient]
     * @throws [IllegalArgumentException] is thrown when the given [sessionRequest] is not recognised
     */
    fun createClient(sessionRequest: SessionRequest): SessionClient {
        when (sessionRequest) {
            is CardSessionRequest -> {
                return CardSessionClient(
                    CardSessionResponseDeserializer(),
                    CardSessionRequestSerializer(),
                    HttpsClient()
                )
            }
            is CvcSessionRequest -> {
                return CvcSessionClient(
                    CvcSessionResponseDeserializer(),
                    CvcSessionRequestSerializer(),
                    HttpsClient()
                )
            }
            else -> {
                throw IllegalArgumentException("unknown session request type found")
            }
        }
    }

}
