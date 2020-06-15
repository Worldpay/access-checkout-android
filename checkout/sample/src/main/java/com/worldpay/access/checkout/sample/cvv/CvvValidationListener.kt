package com.worldpay.access.checkout.sample.cvv

import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.sample.R

class CvvValidationListener(private val activity: FragmentActivity) : AccessCheckoutCvvValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)

    override fun onCvvValidated(isValid: Boolean) {
        val cvv = activity.findViewById<EditText>(R.id.cvv_flow_text_cvv)
        changeFont(cvv, isValid)
        if (!isValid) toggleSubmit(false)
    }

    override fun onValidationSuccess() {
        toggleSubmit(true)
    }

    private fun changeFont(editText: EditText, isValid: Boolean) {
        if (isValid) {
            editText.setTextColor(validColor)
        } else {
            editText.setTextColor(invalidColor)
        }
    }

    private fun toggleSubmit(enabled: Boolean) {
        val submitBtn = activity.findViewById<Button>(R.id.cvv_flow_btn_submit)
        submitBtn.isEnabled = enabled

        if (submitBtn.isEnabled) {
            val submitBtnColor = getColor(activity.resources, R.color.colorPrimary, null)
            submitBtn.setBackgroundColor(submitBtnColor)
        } else {
            val submitBtnColor = getColor(activity.resources, android.R.color.darker_gray, null)
            submitBtn.setBackgroundColor(submitBtnColor)
        }
    }

}
