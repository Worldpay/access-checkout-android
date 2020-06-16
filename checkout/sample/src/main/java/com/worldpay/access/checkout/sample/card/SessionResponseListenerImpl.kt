package com.worldpay.access.checkout.sample.card

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.PANLayout

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {
        debugLog(javaClass.simpleName, "Received session reference map: $sessionResponseMap")

        progressBar.stopLoading()

        AlertDialog.Builder(activity)
            .setTitle("Response")
            .setMessage(sessionResponseMap.toString())
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()

        resetFields()
        setEnabledState(submitBtn = false)
    }

    override fun onError(error: AccessCheckoutException) {
        debugLog(javaClass.simpleName, "Received error: ${error.message}")

        progressBar.stopLoading()

        AlertDialog.Builder(activity)
            .setTitle("Error")
            .setMessage(error.message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()


        setEnabledState(submitBtn = true)
    }

    private fun setEnabledState(submitBtn: Boolean) {
        debugLog(javaClass.simpleName, "Setting enabled state for all fields to : true")
        activity.findViewById<EditText>(R.id.card_flow_text_pan).isEnabled = true
        activity.findViewById<TextView>(R.id.card_flow_text_cvv).isEnabled = true
        activity.findViewById<EditText>(R.id.card_flow_expiry_date).isEnabled = true
        activity.findViewById<Switch>(R.id.card_flow_payments_cvc_switch).isEnabled = true

        debugLog(javaClass.simpleName, "Setting enabled state for submit button to : $submitBtn")
        activity.findViewById<Button>(R.id.card_flow_btn_submit).isEnabled = submitBtn
    }

    private fun resetFields() {
        debugLog(javaClass.simpleName, "Resetting all fields")
        activity.findViewById<EditText>(R.id.card_flow_text_pan).text.clear()
        activity.findViewById<EditText>(R.id.card_flow_text_cvv).text.clear()
        activity.findViewById<EditText>(R.id.card_flow_expiry_date).text.clear()
        activity.findViewById<Switch>(R.id.card_flow_payments_cvc_switch).isChecked = false
    }

}
