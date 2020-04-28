package com.worldpay.access.checkout.api.discovery

import java.io.Serializable

internal class DiscoverLinks(val endpoints: List<Endpoint>): Serializable {

    companion object {
        val verifiedTokens = DiscoverLinks(listOf(Endpoint("service:verifiedTokens"), Endpoint("verifiedTokens:sessions")))
        val sessions =  DiscoverLinks(listOf(Endpoint("service:sessions"), Endpoint("sessions:paymentsCvc")))
    }

}

