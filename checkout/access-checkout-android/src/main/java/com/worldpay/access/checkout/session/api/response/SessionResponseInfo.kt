package com.worldpay.access.checkout.session.api.response

import com.worldpay.access.checkout.client.SessionType
import java.io.Serializable

/**
 * [SessionResponseInfo] class to hold information from the Session Response
 *
 * @param responseBody - [SessionResponse] deserialised session response data
 * @param sessionType - the [SessionType] that was requested
 */
internal class SessionResponseInfo private constructor(
    val responseBody: SessionResponse,
    val sessionType: SessionType
): Serializable {

    internal data class Builder(
        private var responseBody: SessionResponse? = null,
        private var sessionType: SessionType? = null
    ) {

        fun responseBody(responseBody: SessionResponse?) = apply { this.responseBody = responseBody }

        fun sessionType(sessionType: SessionType) = apply { this.sessionType = sessionType }

        fun build() =
            SessionResponseInfo(
                responseBody as SessionResponse,
                sessionType as SessionType
            )
    }

}