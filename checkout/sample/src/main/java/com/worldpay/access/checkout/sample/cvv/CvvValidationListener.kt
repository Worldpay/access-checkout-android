package com.worldpay.access.checkout.sample.cvv

import android.widget.EditText
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.SubmitButton

class CvvValidationListener(private val activity: FragmentActivity) : AccessCheckoutCvvValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)
    private val submitButton = SubmitButton(activity, R.id.cvv_flow_btn_submit)

    override fun onCvvValidated(isValid: Boolean) {
        val cvv = activity.findViewById<EditText>(R.id.cvv_flow_text_cvv)
        changeFont(cvv, isValid)
        if (!isValid) submitButton.disable()
    }

    override fun onValidationSuccess() = submitButton.enable()

    private fun changeFont(editText: EditText, isValid: Boolean) {
        if (isValid) {
            editText.setTextColor(validColor)
        } else {
            editText.setTextColor(invalidColor)
        }
    }

}
