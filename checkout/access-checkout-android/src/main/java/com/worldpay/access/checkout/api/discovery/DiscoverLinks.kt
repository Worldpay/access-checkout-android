package com.worldpay.access.checkout.api.discovery

class DiscoverLinks ( var service: String, var endpoint: String) {

    companion object {
        val verifiedTokens = DiscoverLinks( "service:verifiedTokens", "verifiedTokens:sessions")
        val sessions =  DiscoverLinks("service:sessions", "sessions:paymentsCvc")
    }

}
