package com.worldpay.access.checkout.sample.cvv

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {
        debugLog(javaClass.simpleName, "Received session reference: $sessionResponseMap")

        progressBar.stopLoading()

        AlertDialog.Builder(activity)
            .setTitle("Response")
            .setMessage(sessionResponseMap.toString())
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()

        activity.findViewById<EditText>(R.id.cvv_flow_text_cvv).text.clear()
        setEnabledState(submitBtn = false)
    }

    override fun onError(error: AccessCheckoutException) {
        debugLog(javaClass.simpleName, "Received error: ${error.message}")

        progressBar.stopLoading()

        setEnabledState(submitBtn = false)

        AlertDialog.Builder(activity)
            .setTitle("Error")
            .setMessage(error.message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()

        setEnabledState(submitBtn = true)
    }

    private fun setEnabledState(submitBtn: Boolean) {
        debugLog(javaClass.simpleName, "Setting enabled state for cvv to : true")
        activity.findViewById<TextView>(R.id.cvv_flow_text_cvv).isEnabled = true

        debugLog(javaClass.simpleName, "Setting enabled state for submit button to : $submitBtn")
        activity.findViewById<Button>(R.id.cvv_flow_btn_submit).isEnabled = submitBtn
    }

}
