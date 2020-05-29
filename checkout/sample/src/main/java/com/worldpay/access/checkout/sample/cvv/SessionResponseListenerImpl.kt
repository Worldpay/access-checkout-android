package com.worldpay.access.checkout.sample.cvv

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.TextView
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.SessionResponseListener
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.CardCVVText

class SessionResponseListenerImpl(
    private val activity: Activity,
    private val progressBar: ProgressBar
) : SessionResponseListener {

    override fun onRequestStarted() {
        debugLog(javaClass.simpleName, "Started request")
        progressBar.beginLoading()
        setEnabledState(cvv = false, submitBtn = false)
    }

    override fun onRequestFinished(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?) {
        debugLog(javaClass.simpleName, "Received session reference: $sessionResponseMap")

        progressBar.stopLoading()

        setEnabledState(cvv = true, submitBtn = false)

        val title = getTitle(sessionResponseMap)
        val message = getResponse(sessionResponseMap, error)

        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()

        if (isSuccessful(sessionResponseMap)) {
            activity.findViewById<CardCVVText>(R.id.cvv_flow_text_cvv).text.clear()
            setEnabledState(cvv = true, submitBtn = false)
        } else {
            setEnabledState(cvv = true, submitBtn = true)
        }
    }

    private fun setEnabledState(cvv: Boolean, submitBtn: Boolean) {
        debugLog(javaClass.simpleName, "Setting enabled state for cvv to : $cvv")
        activity.findViewById<TextView>(R.id.cvv_flow_text_cvv).isEnabled = cvv

        debugLog(javaClass.simpleName, "Setting enabled state for submit button to : $submitBtn")
        activity.findViewById<Button>(R.id.cvv_flow_btn_submit).isEnabled = submitBtn
    }

    private fun isSuccessful(sessionResponseMap: Map<SessionType, String>?) =
        sessionResponseMap != null && sessionResponseMap.isNotEmpty()

    private fun getResponse(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?) =
        if (isSuccessful(sessionResponseMap)) sessionResponseMap.toString() else error?.message

    private fun getTitle(sessionResponseMap: Map<SessionType, String>?) =
        if (isSuccessful(sessionResponseMap)) "Response" else "Error"

}