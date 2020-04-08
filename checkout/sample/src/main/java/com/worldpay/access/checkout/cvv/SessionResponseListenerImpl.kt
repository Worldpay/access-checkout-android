package com.worldpay.access.checkout.cvv

import android.app.Activity
import android.widget.TextView
import android.widget.Toast
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.ui.ProgressBar
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.SessionResponseListener

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    override fun onRequestStarted() {
        LoggingUtils.debugLog("CVV Flow", "Started request")
        progressBar.beginLoading()
        toggleLoading(false)
    }

    override fun onRequestFinished(sessionState: String?, error: AccessCheckoutException?) {
        LoggingUtils.debugLog("CVV Flow", "Received session reference: $sessionState")
        progressBar.stopLoading()
        toggleLoading(true)
        val toastMessage: String
        if (!sessionState.isNullOrBlank()) {
            toastMessage = "Ref: $sessionState"
            resetField()
        } else {
            toastMessage = "Error: " + error?.message
        }

        Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun resetField() {
        activity.findViewById<CardCVVText>(R.id.cvv_flow_text_cvv).text.clear()
    }

    private fun toggleLoading(enableFields: Boolean) {
        activity.findViewById<TextView>(R.id.cvv_flow_text_cvv).isEnabled = enableFields
    }

}