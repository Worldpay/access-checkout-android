package com.worldpay.access.checkout.session.api.response

import com.worldpay.access.checkout.client.session.model.SessionType
import java.io.Serializable

/**
 * This class holds the response body and other metadata about the [SessionResponse] and [SessionType]
 *
 * @property[responseBody] [SessionResponse] representation of session response data
 * @property[sessionType] The [SessionType] that was requested
 */
internal class SessionResponseInfo private constructor(
    val responseBody: SessionResponse,
    val sessionType: SessionType
) : Serializable {

    internal data class Builder(
        private var responseBody: SessionResponse? = null,
        private var sessionType: SessionType? = null
    ) {

        fun responseBody(responseBody: SessionResponse?) = apply { this.responseBody = responseBody }

        fun sessionType(sessionType: SessionType) = apply { this.sessionType = sessionType }

        fun build() =
            SessionResponseInfo(
                responseBody!!,
                sessionType!!
            )
    }
}
