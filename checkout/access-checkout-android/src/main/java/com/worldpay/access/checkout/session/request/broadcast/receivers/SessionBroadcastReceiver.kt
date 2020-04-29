package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.SessionResponseListener
import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

internal class SessionBroadcastReceiver() : AbstractSessionBroadcastReceiver() {

    private lateinit var externalSessionResponseListener: SessionResponseListener
    private var numOfSessionTypes = AtomicInteger(0)

    private var numOfSessionRequestCompleted = AtomicInteger(0)

    private lateinit var storedResponses: EnumMap<SessionType, String>

    constructor(externalSessionResponseListener: SessionResponseListener) : this() {
        this.externalSessionResponseListener = externalSessionResponseListener
        this.storedResponses = EnumMap(SessionType::class.java)
    }

    companion object {
        const val NUMBER_OF_SESSION_TYPE_KEY = "num_of_session_types"
        const val RESPONSE_KEY = "response"
        const val SESSION_TYPE_KEY = "session_type"
        const val ERROR_KEY = "error"
    }

    override fun onReceive(context: Context, intent: Intent) {
        debugLog(javaClass.simpleName, "Receiver fired")

        if (intent.action == NUM_OF_SESSION_TYPES_REQUESTED) {
            numOfSessionTypes = AtomicInteger(intent.getIntExtra(NUMBER_OF_SESSION_TYPE_KEY, 0))
        }

        if (intent.action == COMPLETED_SESSION_REQUEST) {
            val response = intent.getSerializableExtra(RESPONSE_KEY)
            val sessionType = intent.getSerializableExtra(SESSION_TYPE_KEY) as SessionType
            val errorSerializable = intent.getSerializableExtra(ERROR_KEY)

            if (numOfSessionTypes.get() == numOfSessionRequestCompleted.incrementAndGet()) {
                response as SessionResponse
                storedResponses[sessionType] = response.links.endpoints.href
                sendSuccessCallback()
            } else {
                if (response is SessionResponse) {
                    storedResponses[sessionType] = response.links.endpoints.href
                } else {
                    sendErrorCallback(errorSerializable)
                }
            }
        }
    }

    private fun sendSuccessCallback() {
        debugLog(javaClass.simpleName, "Intent Resp: $storedResponses")
        externalSessionResponseListener.onRequestFinished(storedResponses.toString(),null)
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
