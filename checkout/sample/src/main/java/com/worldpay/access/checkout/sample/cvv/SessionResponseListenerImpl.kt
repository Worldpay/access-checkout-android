package com.worldpay.access.checkout.sample.cvv

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
import com.worldpay.access.checkout.views.SessionResponseListener

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

        val toastMessage: String = getToastMessage(sessionResponseMap, error)

        if (isSuccessful(sessionResponseMap)) {
            activity.findViewById<CardCVVText>(R.id.cvv_flow_text_cvv).text.clear()
            setEnabledState(cvv = true, submitBtn = false)
        } else {
            setEnabledState(cvv = true, submitBtn = true)
        }

        Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun setEnabledState(cvv: Boolean, submitBtn: Boolean) {
        debugLog(javaClass.simpleName, "Setting enabled state for cvv to : $cvv")
        activity.findViewById<TextView>(R.id.cvv_flow_text_cvv).isEnabled = cvv

        debugLog(javaClass.simpleName, "Setting enabled state for submit button to : $submitBtn")
        activity.findViewById<Button>(R.id.cvv_flow_btn_submit).isEnabled = submitBtn
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

}