package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.session.api.client.ACCEPT_HEADER
import com.worldpay.access.checkout.session.api.client.CONTENT_TYPE_HEADER
import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import com.worldpay.access.checkout.session.api.client.VERIFIED_TOKENS_MEDIA_TYPE
import java.io.Serializable

internal class DiscoverLinks(val endpoints: List<Endpoint>, val headers: Map<String, String>): Serializable {

    companion object {
        val verifiedTokens = DiscoverLinks(
            listOf(
                Endpoint("service:verifiedTokens"),
                Endpoint("verifiedTokens:sessions")
            ),
            mapOf(
                ACCEPT_HEADER to VERIFIED_TOKENS_MEDIA_TYPE,
                CONTENT_TYPE_HEADER to VERIFIED_TOKENS_MEDIA_TYPE
            )
        )
        val sessions =  DiscoverLinks(
            listOf(
                Endpoint("service:sessions"),
                Endpoint("sessions:paymentsCvc")
            ),
            mapOf(
                ACCEPT_HEADER to SESSIONS_MEDIA_TYPE,
                CONTENT_TYPE_HEADER to SESSIONS_MEDIA_TYPE
            )
        )
    }

}

