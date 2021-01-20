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

    override fun onCvcValidated(isValid: Boolean) {
        val cvc = activity.findViewById<EditText>(R.id.restricted_card_flow_text_cvc)
        throwException(cvc)
    }

    override fun onPanValidated(isValid: Boolean) {
        val pan = activity.findViewById<EditText>(R.id.restricted_card_flow_text_pan)
        changeFont(pan, isValid)
    }

    override fun onBrandChange(cardBrand : CardBrand?) {
        val brandLogo = activity.findViewById<ImageView>(R.id.restricted_card_flow_brand_logo)
        getInstance(activity).fetchAndApplyCardLogo(cardBrand, brandLogo)
    }

    override fun onExpiryDateValidated(isValid: Boolean) {
        val exp = activity.findViewById<EditText>(R.id.restricted_card_flow_expiry_date)
        throwException(exp)
    }

    override fun onValidationSuccess() = throw NotImplementedError("This validation success method should never be called.")

    private fun throwException(editText: EditText) {
        throw NotImplementedError("This method should never be called. Text value is: " + editText.text)
    }

    private fun changeFont(editText: EditText, isValid: Boolean) {
        if (isValid) {
            editText.setTextColor(validColor)
        } else {
            editText.setTextColor(invalidColor)
        }
    }

}
