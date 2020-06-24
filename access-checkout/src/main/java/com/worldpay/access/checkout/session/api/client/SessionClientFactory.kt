package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.serialization.CvcSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CvcSessionResponseDeserializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionResponseDeserializer

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
                return VerifiedTokenSessionClient(
                    CardSessionResponseDeserializer(),
                    CardSessionRequestSerializer(),
                    HttpClient()
                )
            }
            is CvcSessionRequest -> {
                return PaymentsCvcSessionClient(
                    CvcSessionResponseDeserializer(),
                    CvcSessionRequestSerializer(),
                    HttpClient()
                )
            }
            else -> {
                throw IllegalArgumentException("unknown session request type found")
            }
        }
    }

}
