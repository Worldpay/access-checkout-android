package com.worldpay.access.checkout.session.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.addResponse
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.allRequestsCompleted
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.getResponses
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.isExpectingResponse
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastDataStore.setNumberOfSessionTypes
import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

internal class SessionBroadcastReceiver() : AbstractSessionBroadcastReceiver() {

    private lateinit var externalSessionResponseListener: SessionResponseListener

    constructor(externalSessionResponseListener: SessionResponseListener) : this() {
        this.externalSessionResponseListener = externalSessionResponseListener
    }

    internal companion object {
        const val NUMBER_OF_SESSION_TYPE_KEY = "num_of_session_types"
        const val RESPONSE_KEY = "response"
        const val ERROR_KEY = "error"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(javaClass.simpleName, "Receiver fired")

        if (intent.action == NUM_OF_SESSION_TYPES_REQUESTED) {
            val num = intent.getIntExtra(NUMBER_OF_SESSION_TYPE_KEY, 0)
            Log.d(javaClass.simpleName, "Received request of $num session types")
            setNumberOfSessionTypes(num)
        }

        if (intent.action == COMPLETED_SESSION_REQUEST && isExpectingResponse()) {
            Log.d(javaClass.simpleName, "Completing session request")
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
                SessionBroadcastDataStore.clear()
            }
        }
    }

    private fun storeResponse(sessionResponseInfo: SessionResponseInfo) {
        Log.d(javaClass.simpleName, "Storing session response")
        addResponse(
            sessionResponseInfo.sessionType,
            sessionResponseInfo.responseBody.links.endpoints.href
        )
    }

    private fun sendSuccessCallback(responses: Map<SessionType, String>) {
        Log.d(javaClass.simpleName, "Sending successful response: $responses")
        externalSessionResponseListener.onSuccess(responses)
    }

    private fun sendErrorCallback(errorSerializable: Serializable) {
        try {
            Log.d(javaClass.simpleName, "Sending erred response: $errorSerializable")
            externalSessionResponseListener.onError(errorSerializable as AccessCheckoutException)
        } catch (ex: Exception) {
            externalSessionResponseListener.onError(
                AccessCheckoutException("Unknown error", ex)
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

    private var numOfSessionTypes = AtomicInteger(0)
    private var storedResponses: EnumMap<SessionType, String> = EnumMap(SessionType::class.java)

    fun setNumberOfSessionTypes(num: Int) {
        numOfSessionTypes.set(num)
    }

    fun isExpectingResponse() = numOfSessionTypes.get() != 0

    fun allRequestsCompleted() = isExpectingResponse() && numOfSessionTypes.get() == storedResponses.size

    fun addResponse(sessionType: SessionType, href: String) {
        storedResponses[sessionType] = href
    }

    fun getResponses(): EnumMap<SessionType, String> = storedResponses

    fun clear() {
        storedResponses.clear()
        numOfSessionTypes.set(0)
    }

}
