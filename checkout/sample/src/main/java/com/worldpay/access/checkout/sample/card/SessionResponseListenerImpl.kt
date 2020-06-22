package com.worldpay.access.checkout.sample.card

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.sample.ui.SubmitButton

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    private val submitBtn = SubmitButton(activity, R.id.card_flow_btn_submit)

    override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {
        Log.d(javaClass.simpleName, "Received session reference map: $sessionResponseMap")

        progressBar.stopLoading()

        AlertDialog.Builder(activity)
            .setTitle("Response")
            .setMessage(sessionResponseMap.toString())
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()

        resetFields()
        enableFields()
        submitBtn.disable()
    }

    override fun onError(error: AccessCheckoutException) {
        Log.d(javaClass.simpleName, "Received error: ${error.message}")

        progressBar.stopLoading()

        AlertDialog.Builder(activity)
            .setTitle("Error")
            .setMessage(error.message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()


        enableFields()
        submitBtn.enable()
    }

    private fun enableFields() {
        Log.d(javaClass.simpleName, "Setting enabled state for all fields to : true")
        activity.findViewById<EditText>(R.id.card_flow_text_pan).isEnabled = true
        activity.findViewById<TextView>(R.id.card_flow_text_cvc).isEnabled = true
        activity.findViewById<EditText>(R.id.card_flow_expiry_date).isEnabled = true
        activity.findViewById<Switch>(R.id.card_flow_payments_cvc_switch).isEnabled = true
    }

    private fun resetFields() {
        Log.d(javaClass.simpleName, "Resetting all fields")
        activity.findViewById<EditText>(R.id.card_flow_text_pan).text.clear()
        activity.findViewById<EditText>(R.id.card_flow_text_cvc).text.clear()
        activity.findViewById<EditText>(R.id.card_flow_expiry_date).text.clear()
        activity.findViewById<Switch>(R.id.card_flow_payments_cvc_switch).isChecked = false
    }

}
