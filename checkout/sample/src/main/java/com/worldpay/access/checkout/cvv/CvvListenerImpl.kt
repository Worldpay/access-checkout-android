package com.worldpay.access.checkout.cvv

import android.app.Activity
import android.text.InputFilter
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isInvisible
import com.worldpay.access.checkout.Card
import com.worldpay.access.checkout.CardListener
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.views.CardView

class CvvListenerImpl(
    private val activity: Activity,
    private val card: Card
) : CardListener {

    override fun onUpdate(cardView: CardView, valid: Boolean) {
        cardView.isValid(valid)

        val submitBtn = activity.findViewById<Button>(R.id.cvv_flow_btn_submit)
        val progressBar = activity.findViewById<ProgressBar>(R.id.loading_bar)

        submitBtn.isEnabled = card.isValid() && progressBar.isInvisible

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

    override fun onUpdateCardBrand(cardBrand: CardBrand?) {
    }

}