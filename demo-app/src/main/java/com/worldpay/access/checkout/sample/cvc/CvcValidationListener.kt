package com.worldpay.access.checkout.sample.cvc

import android.widget.EditText
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.SubmitButton

class CvcValidationListener(private val activity: FragmentActivity) : AccessCheckoutCvcValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)
    private val submitButton = SubmitButton(activity, R.id.cvc_flow_btn_submit)

    override fun onCvcValidated(isValid: Boolean) {
        val cvc = activity.findViewById<EditText>(R.id.cvc_flow_text_cvc)
        changeFont(cvc, isValid)
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
