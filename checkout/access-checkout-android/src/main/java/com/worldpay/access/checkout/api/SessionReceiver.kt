package com.worldpay.access.checkout.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutError
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.SessionResponseListener

internal class SessionReceiver() : BroadcastReceiver() {

    private lateinit var mListener: SessionResponseListener

    constructor(mListener: SessionResponseListener) : this() {
        this.mListener = mListener
    }

    companion object {
        const val RESPONSE_KEY = "response"
        const val ERROR_KEY = "error"

        private const val TAG = "SessionReceiver"
    }


    override fun onReceive(context: Context, intent: Intent) {
        debugLog(TAG, "Receiver fired")
        if (intent.action == SessionRequestService.ACTION_GET_SESSION) {
            debugLog(TAG, "Receiver action: ${intent.action}")
            val response = intent.getSerializableExtra(RESPONSE_KEY)
            val errorSerializable = intent.getSerializableExtra(ERROR_KEY)

            when (response) {
                is SessionResponse -> {
                    mListener.onRequestFinished(response.links.verifiedTokensSession.href, null)
                }
                else -> {
                    try {
                        errorSerializable.let { mListener.onRequestFinished(null, errorSerializable as AccessCheckoutException) }
                    } catch (ex: Exception) {
                        mListener.onRequestFinished(null, AccessCheckoutError("Unknown error", ex))
                    }
                }
            }

            debugLog(TAG, "Intent Resp: $response")
            debugLog(TAG, "Intent Err: $errorSerializable")
        }
    }
}
