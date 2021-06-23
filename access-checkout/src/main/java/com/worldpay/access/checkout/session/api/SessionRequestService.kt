package com.worldpay.access.checkout.session.api

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.inLifeCycleState
import com.worldpay.access.checkout.session.api.client.SessionClientFactory
import com.worldpay.access.checkout.session.api.request.RequestDispatcherFactory
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.COMPLETED_SESSION_REQUEST
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog

internal class SessionRequestService(factory: Factory = DefaultFactory()) :
    Service(),
    Callback<SessionResponseInfo> {

    internal companion object {
        const val REQUEST_KEY = "request"
    }

    private val sessionRequestSender: SessionRequestSender = factory.getSessionRequestSender(this)
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory = factory.getLocalBroadcastManagerFactory(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val sessionRequestInfo = intent.getSerializableExtra(REQUEST_KEY) as SessionRequestInfo
            sessionRequestSender.sendSessionRequest(sessionRequestInfo, this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent) = null

    override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
        debugLog(javaClass.simpleName, "onResponse received: resp:${response?.responseBody} for session type:${response?.sessionType}/ error: $error")
        debugLog(javaClass.simpleName, "service stopped self")
        broadcastResult(response, error)

        stopSelf()
    }

    private fun broadcastResult(sessionResponseInfo: SessionResponseInfo?, error: Exception?) {
        val broadcastIntent = Intent()
        broadcastIntent.putExtra(SessionBroadcastReceiver.RESPONSE_KEY, sessionResponseInfo)
        broadcastIntent.putExtra(SessionBroadcastReceiver.ERROR_KEY, error)
        broadcastIntent.action = COMPLETED_SESSION_REQUEST

        delay(broadcastIntent)
    }

    private fun delay(broadcastIntent: Intent, maxRetry: Int = 5) {
        if (!inLifeCycleState) {
            localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
            return
        }

        Handler().postDelayed(
            {
                if (maxRetry > 0) {
                    delay(broadcastIntent, maxRetry - 1)
                } else {
                    localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
                }
            },
            500
        )
    }
}

internal interface Factory {
    fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory
    fun getSessionRequestSender(context: Context): SessionRequestSender
}

internal class DefaultFactory : Factory {

    override fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory =
        LocalBroadcastManagerFactory(context)

    override fun getSessionRequestSender(context: Context) =
        SessionRequestSender(
            SessionClientFactory(),
            RequestDispatcherFactory()
        )
}
