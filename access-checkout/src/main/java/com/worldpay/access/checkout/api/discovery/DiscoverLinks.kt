package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.session.api.client.ACCEPT_HEADER
import com.worldpay.access.checkout.session.api.client.CONTENT_TYPE_HEADER
import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import java.io.Serializable

internal class DiscoverLinks(val endpoints: List<Endpoint>) : Serializable {

    internal companion object {
        val cardSessions = DiscoverLinks(
            listOf(
                Endpoint("service:sessions"),
                Endpoint(
                    "sessions:card",
                    mapOf(
                        ACCEPT_HEADER to SESSIONS_MEDIA_TYPE,
                        CONTENT_TYPE_HEADER to SESSIONS_MEDIA_TYPE
                    )
                )
            )
        )

        val cvcSessions = DiscoverLinks(
            listOf(
                Endpoint("service:sessions"),
                Endpoint(
                    "sessions:paymentsCvc",
                    mapOf(
                        ACCEPT_HEADER to SESSIONS_MEDIA_TYPE,
                        CONTENT_TYPE_HEADER to SESSIONS_MEDIA_TYPE
                    )
                )
            )
        )
    }
}
