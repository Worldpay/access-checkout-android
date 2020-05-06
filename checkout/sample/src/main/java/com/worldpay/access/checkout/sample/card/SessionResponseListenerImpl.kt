package com.worldpay.access.checkout.sample.card

import android.app.Activity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout
import com.worldpay.access.checkout.views.SessionResponseListener

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    override fun onRequestStarted() {
        debugLog(javaClass.simpleName, "Started request")
        progressBar.beginLoading()
        toggleLoading(false)
    }

    override fun onRequestFinished(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?) {
        debugLog(javaClass.simpleName, "Received session reference map: $sessionResponseMap")
        progressBar.stopLoading()
        toggleLoading(true)
        val toastMessage: String
        if (sessionResponseMap != null && sessionResponseMap.isNotEmpty()) {
            toastMessage = "Ref: $sessionResponseMap"
            resetFields()
        } else {
            toastMessage = "Error: " + error?.message
        }

        Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun resetFields() {
        debugLog(javaClass.simpleName, "Reset Fields")
        activity.findViewById<PANLayout>(R.id.card_flow_text_pan).mEditText.text.clear()
        activity.findViewById<CardCVVText>(R.id.card_flow_text_cvv).text.clear()
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).monthEditText.text.clear()
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).yearEditText.text.clear()
    }

    private fun toggleLoading(enableFields: Boolean) {
        debugLog(javaClass.simpleName, "Toggling enabled state on all fields to : $enableFields")
        activity.findViewById<PANLayout>(R.id.card_flow_text_pan).mEditText.isEnabled = enableFields
        activity.findViewById<TextView>(R.id.card_flow_text_cvv).isEnabled = enableFields
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).monthEditText.isEnabled = enableFields
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).yearEditText.isEnabled = enableFields
        activity.findViewById<Button>(R.id.card_flow_btn_submit).isEnabled = enableFields
    }

}