package com.worldpay.access.checkout.session.api

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClientFactory
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.session.api.client.SessionClientFactory
import com.worldpay.access.checkout.session.api.request.RequestDispatcherFactory
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog

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
                        AccessCheckoutException("Could not discover URL", error), null
                    )
                }
            }
        }
        apiDiscoveryClient.discover(sessionRequestInfo.baseUrl, callback, sessionRequestInfo.discoverLinks)
    }
}
