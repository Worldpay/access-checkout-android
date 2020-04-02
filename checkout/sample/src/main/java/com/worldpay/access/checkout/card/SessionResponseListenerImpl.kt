package com.worldpay.access.checkout.card

import android.app.Activity
import android.widget.Toast
import com.worldpay.access.checkout.ProgressBar
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout
import com.worldpay.access.checkout.views.SessionResponseListener

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    override fun onRequestStarted() {
        LoggingUtils.debugLog("MainActivity", "Started request")
        progressBar.beginLoading()
    }

    override fun onRequestFinished(sessionState: String?, error: AccessCheckoutException?) {
        LoggingUtils.debugLog("MainActivity", "Received session reference: $sessionState")
        progressBar.stopLoading()
        val toastMessage: String
        if (!sessionState.isNullOrBlank()) {
            toastMessage = "Ref: $sessionState"
            resetFields()
        } else {
            toastMessage = "Error: " + error?.message
        }

        Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun resetFields() {
        activity.findViewById<PANLayout>(R.id.card_flow_text_pan).mEditText.text.clear()
        activity.findViewById<CardCVVText>(R.id.card_flow_text_cvv).text.clear()
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).monthEditText.text.clear()
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).yearEditText.text.clear()
    }

}