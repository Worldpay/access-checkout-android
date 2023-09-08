package com.worldpay.access.checkout.sample.card

import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.images.SVGImageLoader.Companion.getInstance
import com.worldpay.access.checkout.sample.ui.SubmitButton
import com.worldpay.access.checkout.ui.AccessEditText

class CardValidationListener(private val activity: FragmentActivity) : AccessCheckoutCardValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)
    private val submitButton = SubmitButton(activity, R.id.card_flow_btn_submit)

    override fun onCvcValidated(isValid: Boolean) {
        val cvc = activity.findViewById<AccessEditText>(R.id.card_flow_text_cvc)
        changeFont(cvc, isValid)
        if (!isValid) submitButton.disable()
    }

    override fun onPanValidated(isValid: Boolean) {
        val pan = activity.findViewById<AccessEditText>(R.id.card_flow_text_pan)
        changeFont(pan, isValid)
        if (!isValid) submitButton.disable()
    }

    override fun onBrandChange(cardBrand: CardBrand?) {
        val brandLogo = activity.findViewById<ImageView>(R.id.card_flow_brand_logo)
        getInstance(activity).fetchAndApplyCardLogo(cardBrand, brandLogo)
    }

    override fun onExpiryDateValidated(isValid: Boolean) {
        val expiryText = activity.findViewById<AccessEditText>(R.id.card_flow_expiry_date)
        changeFont(expiryText, isValid)
        if (!isValid) submitButton.disable()
    }

    override fun onValidationSuccess() = submitButton.enable()

    private fun changeFont(editText: AccessEditText, isValid: Boolean) {
        if (isValid) {
            editText.setTextColor(validColor)
        } else {
            editText.setTextColor(invalidColor)
        }
    }
}
