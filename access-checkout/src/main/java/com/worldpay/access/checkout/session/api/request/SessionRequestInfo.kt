package com.worldpay.access.checkout.session.api.request

import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.model.SessionType
import java.io.Serializable

/**
 * This is a serializable class that contain all the necessary information to request a session.
 * It should only be created using the [SessionRequestInfo.Builder]
 *
 * @param[requestBody] [SessionRequest] with the card and merchant information
 * @param[sessionType] [SessionType] that is being requested
 * @param[discoverLinks] [DiscoverLinks] containing the endpoints to be discovered in the API
 */
internal class SessionRequestInfo private constructor(
    val requestBody: SessionRequest,
    val sessionType: SessionType,
    val discoverLinks: DiscoverLinks
) : Serializable {

    /**
     * A builder for constructing a [SessionRequestInfo]
     */
    internal data class Builder(
        private var requestBody: SessionRequest? = null,
        private var sessionType: SessionType? = null,
        private var discoverLinks: DiscoverLinks? = null
    ) {

        fun requestBody(requestBody: SessionRequest) = apply { this.requestBody = requestBody }

        fun sessionType(sessionType: SessionType) = apply { this.sessionType = sessionType }

        fun discoverLinks(discoverLinks: DiscoverLinks) =
            apply { this.discoverLinks = discoverLinks }

        fun build() =
            SessionRequestInfo(
                requestBody!!,
                sessionType!!,
                discoverLinks!!
            )
    }
}
