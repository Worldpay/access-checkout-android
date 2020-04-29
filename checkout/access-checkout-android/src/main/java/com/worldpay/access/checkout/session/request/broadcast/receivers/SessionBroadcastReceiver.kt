package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.api.session.SessionResponseInfo
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
        const val ERROR_KEY = "error"
    }

    override fun onReceive(context: Context, intent: Intent) {
        debugLog(javaClass.simpleName, "Receiver fired")

        if (intent.action == NUM_OF_SESSION_TYPES_REQUESTED) {
            numOfSessionTypes = AtomicInteger(intent.getIntExtra(NUMBER_OF_SESSION_TYPE_KEY, 0))
        }

        if (intent.action == COMPLETED_SESSION_REQUEST) {
            val sessionResponseInfo = intent.getSerializableExtra(RESPONSE_KEY)
            val errorSerializable = intent.getSerializableExtra(ERROR_KEY)

            if (sessionResponseInfo !is SessionResponseInfo) {
                sendErrorCallback(errorSerializable)
                return
            }

            val allRequestsCompleted = numOfSessionTypes.get() == numOfSessionRequestCompleted.incrementAndGet()
            storedResponses[sessionResponseInfo.sessionType] = sessionResponseInfo.responseBody.links.endpoints.href
            if (allRequestsCompleted) {
                sendSuccessCallback()
            }
        }
    }

    private fun sendSuccessCallback() {
        debugLog(javaClass.simpleName, "Intent Resp: $storedResponses")
        externalSessionResponseListener.onRequestFinished(storedResponses,null)
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
