package com.worldpay.access.checkout.sample.card

import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.images.SVGImageLoader.Companion.getInstance

class RestrictedCardValidationListener(private val activity: FragmentActivity) : AccessCheckoutCardValidationListener {

    private val validColor = getColor(activity.resources, R.color.SUCCESS, null)
    private val invalidColor = getColor(activity.resources, R.color.FAIL, null)

    override fun onCvcValidated(isValid: Boolean) {}

    override fun onPanValidated(isValid: Boolean) {
        val pan = activity.findViewById<EditText>(R.id.restricted_card_flow_text_pan)
        changeFont(pan, isValid)
    }

    override fun onBrandChange(cardBrand: CardBrand?) {
        val brandLogo = activity.findViewById<ImageView>(R.id.restricted_card_flow_brand_logo)
        getInstance(activity).fetchAndApplyCardLogo(cardBrand, brandLogo)
    }

    override fun onExpiryDateValidated(isValid: Boolean) {}

    override fun onValidationSuccess() {}

    private fun changeFont(editText: EditText, isValid: Boolean) {
        if (isValid) {
            editText.setTextColor(validColor)
        } else {
            editText.setTextColor(invalidColor)
        }
    }
}
