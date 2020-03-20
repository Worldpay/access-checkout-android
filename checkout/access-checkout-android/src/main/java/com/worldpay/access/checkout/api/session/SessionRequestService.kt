package com.worldpay.access.checkout.api.session

import android.app.Service
import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.SessionResponse
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog

internal class SessionRequestService(factory: Factory = DefaultFactory()) : Service(),
    Callback<SessionResponse> {

    companion object {

        private const val TAG = "SessionRequestService"

        @JvmStatic
        val ACTION_GET_SESSION = "com.worldpay.access.checkout.api.action.GET_SESSION"

        const val REQUEST_KEY = "request"
        const val BASE_URL_KEY = "base_url"
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
            val cardSessionRequest = intent.getSerializableExtra(REQUEST_KEY)
            val baseUrl = intent.getStringExtra(BASE_URL_KEY)

            sessionRequestSender.sendSessionRequest(cardSessionRequest as CardSessionRequest, baseUrl, this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent) = null

    private fun broadcastResult(response: SessionResponse?, error: Exception?) {
        val broadcastIntent = Intent()
        broadcastIntent.putExtra(SessionReceiver.RESPONSE_KEY, response)
        broadcastIntent.putExtra(SessionReceiver.ERROR_KEY, error)
        broadcastIntent.action =
            ACTION_GET_SESSION

        localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
    }
}

internal interface Factory {
    fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory
    fun getSessionRequestSender(context: Context): SessionRequestSender
}

internal class DefaultFactory :
    Factory {

    override fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory =
        LocalBroadcastManagerFactory(context)

    override fun getSessionRequestSender(context: Context) =
        SessionRequestSender(
            RequestDispatcherFactory()
        )
}
