package com.worldpay.access.checkout.sample.cvc

import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ui.SubmitButton
import com.worldpay.access.checkout.ui.AccessCheckoutEditText

class CvcValidationListener(private val activity: FragmentActivity) : AccessCheckoutCvcValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)
    private val submitButton = SubmitButton(activity, R.id.cvc_flow_btn_submit)

    override fun onCvcValidated(isValid: Boolean) {
        val cvc = activity.findViewById<AccessCheckoutEditText>(R.id.cvc_flow_text_cvc)
        changeFont(cvc, isValid)
        if (!isValid) submitButton.disable()
    }

    override fun onValidationSuccess() = submitButton.enable()

    private fun changeFont(accessCheckoutEditText: AccessCheckoutEditText, isValid: Boolean) {
        if (isValid) {
            accessCheckoutEditText.setTextColor(validColor)
        } else {
            accessCheckoutEditText.setTextColor(invalidColor)
        }
    }
}
