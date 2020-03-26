package com.worldpay.access.checkout.api.discovery

import java.io.Serializable

class DiscoverLinks ( val service: String, val endpoint: String): Serializable {

    companion object {
        val verifiedTokens = DiscoverLinks( "service:verifiedTokens", "verifiedTokens:sessions")
        val sessions =  DiscoverLinks("service:sessions", "sessions:paymentsCvc")
    }

}
