package com.worldpay.access.checkout

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout

class ProgressBar(private val activity: Activity) {

    private var isLoading: Boolean = false

    fun beginLoading() {
        isLoading = true
        toggleLoading(false)
    }

    fun stopLoading() {
        isLoading = false
        toggleLoading(true)
    }

    fun isLoading(): Boolean {
        return isLoading
    }

    private fun toggleLoading(enableFields: Boolean) {
        activity.findViewById<PANLayout>(R.id.card_flow_text_pan).mEditText.isEnabled = enableFields
        activity.findViewById<TextView>(R.id.card_flow_text_cvv).isEnabled = enableFields
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).monthEditText.isEnabled = enableFields
        activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp).yearEditText.isEnabled = enableFields
        activity.findViewById<Button>(R.id.card_flow_btn_submit).isEnabled = enableFields

        fieldsToggle(enableFields)
    }

    private fun fieldsToggle(enableFields: Boolean) {
        val progressBar = activity.findViewById<ProgressBar>(R.id.loading_bar)
        val fragmentCardFlow = activity.findViewById<ConstraintLayout>(R.id.fragment_card_flow)

        if (!enableFields) {
            fragmentCardFlow.alpha = 0.5f
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
            fragmentCardFlow.alpha = 1.0f
        }
    }

}