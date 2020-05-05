package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClientFactory
import com.worldpay.access.checkout.api.session.client.SessionClientFactory
import com.worldpay.access.checkout.api.session.request.RequestDispatcherFactory
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog

internal class SessionRequestSender(
    private val sessionClientFactory: SessionClientFactory,
    private val requestDispatcherFactory: RequestDispatcherFactory,
    private val apiDiscoveryClient: ApiDiscoveryClient = ApiDiscoveryClientFactory.getClient()
) {

    fun sendSessionRequest(
        sessionRequestInfo: SessionRequestInfo,
        sessionResponseCallback: Callback<SessionResponseInfo>
    ) {
        debugLog("SessionRequestSender", "Making session request")
        val callback = object :
            Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                if (response != null) {
                    val sessionClient = sessionClientFactory.createClient(sessionRequestInfo.requestBody)
                    val requestDispatcher = requestDispatcherFactory.getInstance(response, sessionClient, sessionResponseCallback)
                    requestDispatcher.execute(sessionRequestInfo)
                } else {
                    sessionResponseCallback.onResponse(
                        AccessCheckoutDiscoveryException("Could not discover URL", error), null
                    )
                }
            }
        }
        apiDiscoveryClient.discover(sessionRequestInfo.baseUrl, callback, sessionRequestInfo.discoverLinks)
    }
}