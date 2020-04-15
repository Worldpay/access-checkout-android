package com.worldpay.access.checkout.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.AccessCheckoutCard
import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.configuration.CardConfigurationFactory
import com.worldpay.access.checkout.card.CardListenerImpl
import com.worldpay.access.checkout.card.SessionResponseListenerImpl
import com.worldpay.access.checkout.client.card.CardDetailsBuilder
import com.worldpay.access.checkout.client.checkout.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout
import kotlin.properties.Delegates

class CardFlowFragment : Fragment() {

    private var submitBtnEnabledColor by Delegates.notNull<Int>()
    private var submitBtnDisabledColor by Delegates.notNull<Int>()

    private lateinit var panView: PANLayout
    private lateinit var cvvText: CardCVVText
    private lateinit var expiryText: CardExpiryTextLayout
    private lateinit var submitBtn: Button

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            progressBar = ProgressBar(activity)

            panView = view.findViewById(R.id.card_flow_text_pan)
            expiryText = view.findViewById(R.id.card_flow_text_exp)
            cvvText = view.findViewById(R.id.card_flow_text_cvv)
            submitBtn = view.findViewById(R.id.card_flow_btn_submit)

            submitBtnEnabledColor =
                getColor(activity.resources, R.color.colorPrimary, null)

            submitBtnDisabledColor =
                getColor(activity.resources, android.R.color.darker_gray, null)

            initialiseCardValidation(activity)

            initialisePaymentFlow(activity)
        }
    }

    override fun onResume() {
        toggleFields(!progressBar.isLoading())
        super.onResume()
    }

    private fun initialisePaymentFlow(activity: FragmentActivity) {
        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(getBaseUrl())
            .merchantId(getMerchantID())
            .sessionResponseListener(SessionResponseListenerImpl(activity, progressBar))
            .context(activity.applicationContext)
            .lifecycleOwner(this)
            .build()

        submitBtn.setOnClickListener {
            val cardDetails = CardDetailsBuilder()
                .pan(panView.getInsertedText())
                .expiryDate(expiryText.getMonth(), expiryText.getYear())
                .cvv(cvvText.getInsertedText())
                .build()
            accessCheckoutClient.generateSessionState(cardDetails)
        }
    }

    private fun initialiseCardValidation(activity: FragmentActivity) {
        val card = AccessCheckoutCard(panView, cvvText, expiryText)
        card.cardListener = CardListenerImpl(activity, card)
        card.cardValidator = AccessCheckoutCardValidator()

        CardConfigurationFactory.getRemoteCardConfiguration(card, getBaseUrl())

        panView.cardViewListener = card
        cvvText.cardViewListener = card
        expiryText.cardViewListener = card
    }

    private fun toggleFields(enableFields: Boolean) {
        panView.mEditText.isEnabled = enableFields
        cvvText.isEnabled = enableFields
        expiryText.monthEditText.isEnabled = enableFields
        expiryText.yearEditText.isEnabled = enableFields
        submitBtn.isEnabled = false
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)

}