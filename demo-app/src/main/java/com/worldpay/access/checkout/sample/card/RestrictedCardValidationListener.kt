package com.worldpay.access.checkout.sample.card

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.images.SVGImageLoader.Companion.getInstance
import com.worldpay.access.checkout.ui.AccessEditText

class RestrictedCardValidationListener(private val activity: FragmentActivity) : AccessCheckoutCardValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)

    override fun onCvcValidated(isValid: Boolean) {
        // added to implement the interface
    }

    override fun onPanValidated(isValid: Boolean) {
        val pan = activity.findViewById<AccessEditText>(R.id.restricted_card_flow_text_pan)
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

    private fun changeFont(accessEditText: AccessEditText, isValid: Boolean) {
        if (isValid) {
            accessEditText.setTextColor(validColor)
        } else {
            accessEditText.setTextColor(invalidColor)
        }
    }
}
