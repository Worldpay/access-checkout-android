package com.worldpay.access.checkout.sample.card

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.images.SVGImageLoader.Companion.getInstance
import com.worldpay.access.checkout.ui.AccessCheckoutEditText

class RestrictedCardValidationListener(private val activity: FragmentActivity) : AccessCheckoutCardValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)

    override fun onCvcValidated(isValid: Boolean) {
        // added to implement the interface
    }

    override fun onPanValidated(isValid: Boolean) {
        val pan = activity.findViewById<AccessCheckoutEditText>(R.id.restricted_card_flow_text_pan)
        changeFont(pan, isValid)
    }

    override fun onBrandChange(cardBrand: CardBrand?) {
        val brandLogo = activity.findViewById<ImageView>(R.id.restricted_card_flow_brand_logo)
        getInstance(activity).fetchAndApplyCardLogo(cardBrand, brandLogo)
    }

    override fun onExpiryDateValidated(isValid: Boolean) {
        // added to implement the interface
    }

    override fun onValidationSuccess() {
        // added to implement the interface
    }

    private fun changeFont(accessCheckoutEditText: AccessCheckoutEditText, isValid: Boolean) {
        if (isValid) {
            accessCheckoutEditText.setTextColor(validColor)
        } else {
            accessCheckoutEditText.setTextColor(invalidColor)
        }
    }
}
