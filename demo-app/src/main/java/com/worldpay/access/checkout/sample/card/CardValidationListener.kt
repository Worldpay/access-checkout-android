package com.worldpay.access.checkout.sample.card

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.images.SVGImageLoader.Companion.getInstance
import com.worldpay.access.checkout.sample.ui.SubmitButton
import com.worldpay.access.checkout.ui.AccessCheckoutEditText

class CardValidationListener(private val activity: FragmentActivity) :
    AccessCheckoutCardValidationListener {

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

    override fun onBrandsChange(cardBrands: List<CardBrand>) {
        val brandLogo = activity.findViewById<ImageView>(R.id.card_flow_brand_logo)
        if (brandLogo != null) {
            // currently just applying first card logo returned in list
            // as underlying functionality to display two logos doesn't exist yet
            // will be displayed in demo application as csv values below card number field
            val cardBrand = if (cardBrands.isEmpty()) null else cardBrands.first()
            getInstance(activity).fetchAndApplyCardLogo(cardBrand, brandLogo)
            val cardBrandList =
                if (cardBrands.isEmpty()) null else cardBrands.joinToString(", ") { it.name }

            // we then set the text of the list of card brands to the text view
            if (cardBrandList != null) setCardBrandText(cardBrandList) else setCardBrandText("")
        } else {
            Log.d(
                this::class.java.simpleName,
                "Received CardBrand change but could not find ImageView with id `R.id.card_flow_brand_logo`"
            )
        }
    }

    override fun onExpiryDateValidated(isValid: Boolean) {
        val expiryText = activity.findViewById<AccessCheckoutEditText>(R.id.card_flow_expiry_date)
        changeFont(expiryText, isValid)
        if (!isValid) enableSubmitButton(false)
    }

    override fun onValidationSuccess() = enableSubmitButton(true)

    private fun changeFont(accessCheckoutEditText: AccessCheckoutEditText, isValid: Boolean) {
        if (isValid) {
            accessCheckoutEditText.setTextColor(validColor)
        } else {
            accessCheckoutEditText.setTextColor(invalidColor)
        }
    }

    private fun setCardBrandText(text: String?) {
        val cardBrandNameTextView =
            activity.findViewById<TextView>(R.id.card_flow_text_card_brand_name)
        cardBrandNameTextView.text = text
    }

    private fun enableSubmitButton(enable: Boolean) {
        if (enable) {
            activity.runOnUiThread {
                submitButton.enable()
            }
        } else {
            activity.runOnUiThread {
                submitButton.disable()
            }
        }

    }
}
