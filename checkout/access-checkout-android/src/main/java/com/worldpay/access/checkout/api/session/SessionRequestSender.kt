package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClient
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClientFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.client.SessionClientFactory
import com.worldpay.access.checkout.api.session.request.RequestDispatcherFactory
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog

internal class SessionRequestSender(
    private val sessionClientFactory: SessionClientFactory,
    private val requestDispatcherFactory: RequestDispatcherFactory,
    private val accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClientFactory.getClient()
) {

    fun sendSessionRequest(
        sessionRequest: SessionRequest,
        baseUrl: String,
        sessionResponseCallback: Callback<SessionResponse>,
        discoverLinks: DiscoverLinks
    ) {
        debugLog("SessionRequestSender", "Making session request")
        val callback = object :
            Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                if (response != null) {
                    val sessionClient = sessionClientFactory.createClient(sessionRequest)
                    val requestDispatcher = requestDispatcherFactory.getInstance(response, sessionClient, sessionResponseCallback)
                    requestDispatcher.execute(sessionRequest)
                } else {
                    sessionResponseCallback.onResponse(
                        AccessCheckoutDiscoveryException("Could not discover URL", error), null
                    )
                }
            }
        }
        accessCheckoutDiscoveryClient.discover(baseUrl, callback, discoverLinks)
    }
}