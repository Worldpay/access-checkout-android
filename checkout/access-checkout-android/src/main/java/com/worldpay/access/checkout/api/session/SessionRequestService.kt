package com.worldpay.access.checkout.api.session

import android.app.Service
import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.client.SessionClientFactory
import com.worldpay.access.checkout.api.session.request.RequestDispatcherFactory
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.session.request.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionBroadcastReceiver

internal class SessionRequestService(factory: Factory = DefaultFactory()) : Service(),
    Callback<SessionResponse> {

    companion object {

        private const val TAG = "SessionRequestService"

        @JvmStatic
        val ACTION_GET_SESSION = "com.worldpay.access.checkout.api.action.GET_SESSION"

        const val DISCOVER_LINKS = "discover"
        const val REQUEST_KEY = "request"
        const val BASE_URL_KEY = "base_url"
        const val SESSION_TYPE = "session_type"
    }

    private val sessionRequestSender: SessionRequestSender = factory.getSessionRequestSender(this)
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory = factory.getLocalBroadcastManagerFactory(this)

    override fun onResponse(error: Exception?, response: SessionResponse?) {
        debugLog(TAG, "onResponse received: resp:$response / error: $error")
        debugLog(TAG, "service stopped self")
        broadcastResult(response, error)

        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val sessionRequest = intent.getSerializableExtra(REQUEST_KEY) as SessionRequest
            val baseUrl = intent.getStringExtra(BASE_URL_KEY)
            val discoverLinks = intent.getSerializableExtra(DISCOVER_LINKS) as DiscoverLinks
            sessionRequestSender.sendSessionRequest(sessionRequest, baseUrl, this, discoverLinks)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent) = null

    private fun broadcastResult(response: SessionResponse?, error: Exception?) {
        val broadcastIntent = Intent()
        broadcastIntent.putExtra(SessionBroadcastReceiver.RESPONSE_KEY, response)
        broadcastIntent.putExtra(SessionBroadcastReceiver.ERROR_KEY, error)
        broadcastIntent.action = ACTION_GET_SESSION

        localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
    }
}

internal interface Factory {
    fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory
    fun getSessionRequestSender(context: Context): SessionRequestSender
}

internal class DefaultFactory: Factory {

    override fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory =
        LocalBroadcastManagerFactory(context)

    override fun getSessionRequestSender(context: Context) =
        SessionRequestSender(SessionClientFactory(), RequestDispatcherFactory())

}
