package com.worldpay.access.checkout.sample.cvc

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.sample.ui.SubmitButton

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    private val submitBtn = SubmitButton(activity, R.id.cvc_flow_btn_submit)

    override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {
        Log.d(javaClass.simpleName, "Received session reference: $sessionResponseMap")

        progressBar.stopLoading()

        AlertDialog.Builder(activity)
            .setTitle("Response")
            .setMessage(sessionResponseMap.toString())
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()

        activity.findViewById<EditText>(R.id.cvc_flow_text_cvc).text.clear()
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
        Log.d(javaClass.simpleName, "Setting enabled state for cvc to : true")
        activity.findViewById<TextView>(R.id.cvc_flow_text_cvc).isEnabled = true
    }
}
