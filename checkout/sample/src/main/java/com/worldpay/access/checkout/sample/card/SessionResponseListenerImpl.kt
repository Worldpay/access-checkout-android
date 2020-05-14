package com.worldpay.access.checkout.sample.card

import android.app.Activity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
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
        setEnabledState(allFields = false, submitBtn = false)
    }

    override fun onRequestFinished(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?) {
        debugLog(javaClass.simpleName, "Received session reference map: $sessionResponseMap")

        progressBar.stopLoading()

        val toastMessage: String = getToastMessage(sessionResponseMap, error)

        if (isSuccessful(sessionResponseMap)) {
            resetFields()
            setEnabledState(allFields = true, submitBtn = false)
        } else {
            setEnabledState(allFields = true, submitBtn = true)
        }

        Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun isSuccessful(sessionResponseMap: Map<SessionType, String>?) =
        sessionResponseMap != null && sessionResponseMap.isNotEmpty()

    private fun getToastMessage(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?): String {
        return if (isSuccessful(sessionResponseMap)) {
            "Ref: $sessionResponseMap"
        } else {
            "Error: " + error?.message
        }
    }

    private fun setEnabledState(allFields: Boolean, submitBtn: Boolean) {
        debugLog(javaClass.simpleName, "Setting enabled state for all fields to : $allFields")
        activity.findViewById<PANLayout>(R.id.card_flow_text_pan).mEditText.isEnabled = allFields
        activity.findViewById<TextView>(R.id.card_flow_text_cvv).isEnabled = allFields
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).monthEditText.isEnabled = allFields
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).yearEditText.isEnabled = allFields

        debugLog(javaClass.simpleName, "Setting enabled state for submit button to : $submitBtn")
        activity.findViewById<Button>(R.id.card_flow_btn_submit).isEnabled = submitBtn
    }

    private fun resetFields() {
        debugLog(javaClass.simpleName, "Resetting all fields")
        activity.findViewById<PANLayout>(R.id.card_flow_text_pan).mEditText.text.clear()
        activity.findViewById<CardCVVText>(R.id.card_flow_text_cvv).text.clear()
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).monthEditText.text.clear()
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).yearEditText.text.clear()
    }

}
