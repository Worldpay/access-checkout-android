package com.worldpay.access.checkout.sample.card

import android.util.Log
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.images.SVGImageLoader.Companion.getInstance
import com.worldpay.access.checkout.sample.ui.SubmitButton
import com.worldpay.access.checkout.ui.AccessCheckoutEditText

class CardValidationListener(private val activity: FragmentActivity) : AccessCheckoutCardValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)
    private val submitButton = SubmitButton(activity, R.id.card_flow_btn_submit)

    override fun onCvcValidated(isValid: Boolean) {
        val cvc = activity.findViewById<AccessCheckoutEditText>(R.id.card_flow_text_cvc)
        changeFont(cvc, isValid)
        if (!isValid) submitButton.disable()
    }

    override fun onPanValidated(isValid: Boolean) {
        val pan = activity.findViewById<AccessCheckoutEditText>(R.id.card_flow_text_pan)
        changeFont(pan, isValid)
        if (!isValid) submitButton.disable()
    }

    override fun onBrandChange(cardBrand: CardBrand?) {
        val brandLogo = activity.findViewById<ImageView>(R.id.card_flow_brand_logo)
        if (brandLogo != null) {
            getInstance(activity).fetchAndApplyCardLogo(cardBrand, brandLogo)
        } else {
            Log.d(this::class.java.simpleName, "Received CardBranch change but could not find ImageView with id `R.id.card_flow_brand_logo`")
        }
    }

    override fun onExpiryDateValidated(isValid: Boolean) {
        val expiryText = activity.findViewById<AccessCheckoutEditText>(R.id.card_flow_expiry_date)
        changeFont(expiryText, isValid)
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
