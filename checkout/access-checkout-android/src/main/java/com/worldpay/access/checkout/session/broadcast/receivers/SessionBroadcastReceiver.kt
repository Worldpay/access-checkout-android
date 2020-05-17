package com.worldpay.access.checkout.session.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.session.api.SessionResponseInfo
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.addResponse
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.allRequestsCompleted
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.getResponses
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.setNumberOfSessionTypes
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.SessionResponseListener
import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

internal class SessionBroadcastReceiver() : AbstractSessionBroadcastReceiver() {

    private lateinit var externalSessionResponseListener: SessionResponseListener

    constructor(externalSessionResponseListener: SessionResponseListener) : this() {
        this.externalSessionResponseListener = externalSessionResponseListener
    }

    companion object {
        const val NUMBER_OF_SESSION_TYPE_KEY = "num_of_session_types"
        const val RESPONSE_KEY = "response"
        const val ERROR_KEY = "error"
    }

    override fun onReceive(context: Context, intent: Intent) {
        debugLog(javaClass.simpleName, "Receiver fired")

        if (intent.action == NUM_OF_SESSION_TYPES_REQUESTED) {
            setNumberOfSessionTypes(intent.getIntExtra(NUMBER_OF_SESSION_TYPE_KEY, 0))
        }

        if (intent.action == COMPLETED_SESSION_REQUEST) {
            val sessionResponseInfo = intent.getSerializableExtra(RESPONSE_KEY)
            val errorSerializable = intent.getSerializableExtra(ERROR_KEY)

            if (sessionResponseInfo is SessionResponseInfo) {
                storeResponse(sessionResponseInfo)
                if (allRequestsCompleted()) {
                    sendSuccessCallback(getResponses())
                    SessionBroadcastDataStore.clear()
                }
            } else {
                sendErrorCallback(errorSerializable)
            }
        }
    }

    private fun storeResponse(sessionResponseInfo: SessionResponseInfo) {
        addResponse(
            sessionResponseInfo.sessionType,
            sessionResponseInfo.responseBody.links.endpoints.href
        )
    }

    private fun sendSuccessCallback(responses: Map<SessionType, String>) {
        debugLog(javaClass.simpleName, "Intent Resp: $responses")
        externalSessionResponseListener.onRequestFinished(responses,null)
    }

    private fun sendErrorCallback(errorSerializable: Serializable?) {
        try {
            debugLog(javaClass.simpleName, "Intent Err: $errorSerializable")
            errorSerializable.let {
                externalSessionResponseListener.onRequestFinished(
                    null,
                    errorSerializable as AccessCheckoutException
                )
            }
        } catch (ex: Exception) {
            externalSessionResponseListener.onRequestFinished(
                null,
                AccessCheckoutError("Unknown error", ex)
            )
        }
    }

    override fun getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(NUM_OF_SESSION_TYPES_REQUESTED)
        intentFilter.addAction(SESSION_TYPE_REQUEST_COMPLETE)
        intentFilter.addAction(COMPLETED_SESSION_REQUEST)
        return intentFilter
    }

}

internal object SessionBroadcastDataStore {

    private var numOfSessionRequestCompleted = AtomicInteger(0)
    private var numOfSessionTypes = AtomicInteger(0)
    private var storedResponses: EnumMap<SessionType, String> = EnumMap(SessionType::class.java)

    fun setNumberOfSessionTypes(num: Int) {
        numOfSessionTypes.set(num)
    }

    fun allRequestsCompleted() = numOfSessionTypes.get() == numOfSessionRequestCompleted.incrementAndGet()

    fun addResponse(sessionType: SessionType, href: String) {
        storedResponses[sessionType] = href
    }

    fun getResponses(): EnumMap<SessionType, String> = storedResponses

    fun clear() {
        storedResponses.clear()
        numOfSessionTypes.set(0)
        numOfSessionRequestCompleted.set(0)
    }

}
