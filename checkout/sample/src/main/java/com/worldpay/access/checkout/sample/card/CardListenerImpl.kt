package com.worldpay.access.checkout.sample.card

import android.app.Activity
import android.text.InputFilter
import android.widget.Button
import androidx.core.content.res.ResourcesCompat.getColor
import com.worldpay.access.checkout.Card
import com.worldpay.access.checkout.CardListener
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.images.SVGImageLoader
import com.worldpay.access.checkout.sample.ui.ProgressBar
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.CardView
import com.worldpay.access.checkout.views.PANLayout

class CardListenerImpl(
    private val activity: Activity,
    private val card: Card,
    private val progressBar: ProgressBar
) : CardListener {

    override fun onUpdate(cardView: CardView, valid: Boolean) {
        cardView.isValid(valid)

        val submitBtn = activity.findViewById<Button>(R.id.card_flow_btn_submit)

        if (submitBtn == null) {
            debugLog(javaClass.simpleName, "Could not find submit button")
            return
        }

        submitBtn.isEnabled = card.isValid() && !progressBar.isLoading()

        if (submitBtn.isEnabled) {
            val submitBtnColor = getColor(activity.resources, R.color.colorPrimary, null)
            submitBtn.setBackgroundColor(submitBtnColor)
        } else {
            val submitBtnColor = getColor(activity.resources, android.R.color.darker_gray, null)
            submitBtn.setBackgroundColor(submitBtnColor)
        }
    }

    override fun onUpdateLengthFilter(cardView: CardView, inputFilter: InputFilter) {
        cardView.applyLengthFilter(inputFilter)
    }

    override fun onUpdateCardBrand(cardBrand: RemoteCardBrand?) {
        val panView = activity.findViewById<PANLayout>(R.id.card_flow_text_pan) ?: return
        val logoImageView = panView.mImageView
        SVGImageLoader.getInstance(activity).fetchAndApplyCardLogo(cardBrand, logoImageView)
    }

}
