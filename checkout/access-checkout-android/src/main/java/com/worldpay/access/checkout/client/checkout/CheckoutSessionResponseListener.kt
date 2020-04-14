package com.worldpay.access.checkout.client.checkout

import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.views.SessionResponseListener

class CheckoutSessionResponseListener(
    private val tag: String,
    private val externalSessionResponseListener: SessionResponseListener
) : SessionResponseListener {

    override fun onRequestStarted() {}

    override fun onRequestFinished(sessionState: String?, error: AccessCheckoutException?) {
        LoggingUtils.debugLog(tag, "Received session reference")
        externalSessionResponseListener.onRequestFinished(sessionState, error)
    }

}