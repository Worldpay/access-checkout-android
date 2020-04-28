package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.SessionResponseListener

internal class SessionBroadcastReceiver() : AbstractSessionBroadcastReceiver() {

    private lateinit var externalSessionResponseListener: SessionResponseListener

    constructor(mListener: SessionResponseListener) : this() {
        this.externalSessionResponseListener = mListener
    }

    companion object {
        const val RESPONSE_KEY = "response"
        const val ERROR_KEY = "error"
    }

    override fun getIntentFilter(): IntentFilter {
        return IntentFilter(javaClass.name)
    }

    override fun onReceive(context: Context, intent: Intent) {
        debugLog(javaClass.name, "Receiver fired")

        if (intent.action != javaClass.name) {
            return
        }

        val response = intent.getSerializableExtra(RESPONSE_KEY)
        val errorSerializable = intent.getSerializableExtra(ERROR_KEY)

        when (response) {
            is SessionResponse -> {
                externalSessionResponseListener.onRequestFinished(
                    response.links.endpoints.href,
                    null
                )
            }
            else -> {
                try {
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
        }

        debugLog(javaClass.name, "Intent Resp: $response")
        debugLog(javaClass.name, "Intent Err: $errorSerializable")
    }
}
