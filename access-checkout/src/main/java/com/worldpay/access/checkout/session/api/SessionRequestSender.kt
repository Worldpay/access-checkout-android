package com.worldpay.access.checkout.session.api

import android.util.Log
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.session.api.client.SessionClientFactory
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo

internal class SessionRequestSender(
    private val sessionClientFactory: SessionClientFactory,
    private val apiDiscoveryClient: ApiDiscoveryClient = ApiDiscoveryClient()
) {

    suspend fun sendSessionRequest(
        sessionRequestInfo: SessionRequestInfo,
    ): SessionResponseInfo {
        Log.d("SessionRequestSender", "Making session request")

        val endpoint = apiDiscoveryClient.discoverEndpoint(
            sessionRequestInfo.baseUrl,
            sessionRequestInfo.discoverLinks
        )

        val sessionClient = sessionClientFactory.createClient(sessionRequestInfo.requestBody)

        try {
            val responseBody = sessionClient.getSessionResponse(endpoint, sessionRequestInfo.requestBody)
            return SessionResponseInfo.Builder()
                .responseBody(responseBody)
                .sessionType(sessionRequestInfo.sessionType)
                .build()
        } catch (ex: Exception) {
            Log.e("RequestDispatcher", "Received exception: $ex")
            throw ex
        }
    }
}
