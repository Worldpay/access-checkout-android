package com.worldpay.access.checkout.session.api

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.inLifeCycleState
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.sendToMessageQueue
import com.worldpay.access.checkout.session.api.client.SessionClientFactory
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.COMPLETED_SESSION_REQUEST
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver
import com.worldpay.access.checkout.util.coroutine.DispatchersProvider
import com.worldpay.access.checkout.util.coroutine.IDispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SessionRequestService(
    factory: Factory = DefaultFactory(),
    private val dispatchers: IDispatchersProvider = DispatchersProvider.instance
) :
    Service() {

    internal companion object {
        const val REQUEST_KEY = "request"
    }

    private val sessionRequestSender: SessionRequestSender = factory.getSessionRequestSender()
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory =
        factory.getLocalBroadcastManagerFactory(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            CoroutineScope(dispatchers.main).launch {
                try {
                    val sessionResponseInfo =
                        withContext(dispatchers.io) { fetchSessionResponseInfo(intent) }
                    broadcastResult(sessionResponseInfo, null)
                } catch (ex: Exception) {
                    Log.e(SessionRequestService::class.java.simpleName, "Failed to retrieve session", ex)
                    broadcastResult(null, ex)
                } finally {
                    Log.d(SessionRequestService::class.java.simpleName, "service stopped self")
                    stopSelf()
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun fetchSessionResponseInfo(intent: Intent): SessionResponseInfo {
        val sessionRequestInfo = intent.getSerializableExtra(REQUEST_KEY)

        if (sessionRequestInfo !is SessionRequestInfo) {
            throw AccessCheckoutException("Failed to parse request key for sending the session request")
        }

        val sessionResponseInfo = sessionRequestSender.sendSessionRequest(sessionRequestInfo)
        Log.d(
            javaClass.simpleName,
            "session response received: " +
                    "resp:${sessionResponseInfo.responseBody} " +
                    "for session type:${sessionResponseInfo.sessionType}"
        )
        return sessionResponseInfo
    }

    override fun onBind(intent: Intent): IBinder? = null

    private fun broadcastResult(sessionResponseInfo: SessionResponseInfo?, error: Exception?) {
        val broadcastIntent = Intent()
        broadcastIntent.putExtra(SessionBroadcastReceiver.RESPONSE_KEY, sessionResponseInfo)
        broadcastIntent.putExtra(SessionBroadcastReceiver.ERROR_KEY, error)
        broadcastIntent.action = COMPLETED_SESSION_REQUEST

        delay(broadcastIntent)
    }

    private fun delay(broadcastIntent: Intent) {
        if (inLifeCycleState) {
            sendToMessageQueue {
                localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
            }
        } else {
            localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
        }
    }
}

internal interface Factory {
    fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory
    fun getSessionRequestSender(): SessionRequestSender
}

internal class DefaultFactory : Factory {

    override fun getLocalBroadcastManagerFactory(context: Context): LocalBroadcastManagerFactory =
        LocalBroadcastManagerFactory(context)

    override fun getSessionRequestSender() = SessionRequestSender(SessionClientFactory())
}
