package com.worldpay.access.checkout.api.discovery

import java.io.Serializable

class DiscoverLinks ( val endpoints: List<String>): Serializable {

    companion object {
        val verifiedTokens = DiscoverLinks( listOf("service:verifiedTokens", "verifiedTokens:sessions"))
        val sessions =  DiscoverLinks(listOf("service:sessions", "sessions:paymentsCvc"))
    }

}

