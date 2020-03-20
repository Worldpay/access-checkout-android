package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClient
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClientFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog

internal class SessionRequestSender(
    private val requestDispatcherFactory: RequestDispatcherFactory,
    private val accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClientFactory.getClient()
) {

    fun sendSessionRequest(
        cardSessionRequest: CardSessionRequest,
        baseUrl: String,
        sessionResponseCallback: Callback<SessionResponse>
    ) {
        debugLog("SessionRequestSender", "Making session request")
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                if (response != null) {
                    val requestDispatcher = requestDispatcherFactory.getInstance(response, sessionResponseCallback)
                    requestDispatcher.execute(cardSessionRequest)
                } else {
                    sessionResponseCallback.onResponse(
                        AccessCheckoutDiscoveryException("Could not discover URL", error), null
                    )
                }
            }
        }
        accessCheckoutDiscoveryClient.discover(baseUrl, callback, DiscoverLinks.verifiedTokens)
    }
}