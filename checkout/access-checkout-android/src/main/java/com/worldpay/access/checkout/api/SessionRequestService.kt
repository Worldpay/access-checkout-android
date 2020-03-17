package com.worldpay.access.checkout.api

import android.app.Service
import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog

internal class SessionRequestService(factory: Factory = DefaultFactory(), accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient) : Service(), Callback<SessionResponse> {

    companion object {

        private const val TAG = "SessionRequestService"

        @JvmStatic
        val ACTION_GET_SESSION = "com.worldpay.access.checkout.api.action.GET_SESSION"

        const val REQUEST_KEY = "request"
        const val BASE_URL_KEY = "base_url"
    }

    private val sessionRequestSender: SessionRequestSender = factory.getSessionRequestSender(this, accessCheckoutDiscoveryClient)
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory = factory.getLocalBroadcastManagerFactory(this)

    override fun onResponse(error: Exception?, response: SessionResponse?) {
        debugLog(TAG, "onResponse received: resp:$response / error: $error")
        debugLog(TAG, "service stopped self")
        broadcastResult(response, error)

        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val sessionRequest = intent.getSerializableExtra(REQUEST_KEY)
            val baseUrl = intent.getStringExtra(BASE_URL_KEY)

            sessionRequestSender.sendSessionRequest(sessionRequest as SessionRequest, baseUrl, this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent) = null

    private fun broadcastResult(response: SessionResponse?, error: Exception?) {
        val broadcastIntent = Intent()
        broadcastIntent.putExtra(SessionReceiver.RESPONSE_KEY, response)
        broadcastIntent.putExtra(SessionReceiver.ERROR_KEY, error)
        broadcastIntent.action = ACTION_GET_SESSION

        localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
    }
}

internal interface Factory {
    fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory
    fun getSessionRequestSender(context: Context, accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient): SessionRequestSender
}

internal class DefaultFactory : Factory {

    override fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory = LocalBroadcastManagerFactory(context)

    override fun getSessionRequestSender(context: Context, accessCheckoutDiscoveryClient: AccessCheckoutDiscoveryClient) = SessionRequestSender(RequestDispatcherFactory(), accessCheckoutDiscoveryClient)
}
