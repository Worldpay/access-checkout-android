package com.worldpay.access.checkout.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.AccessCheckoutCVV
import com.worldpay.access.checkout.AccessCheckoutCVVClient
import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.configuration.CardConfigurationFactory
import com.worldpay.access.checkout.cvv.CvvListenerImpl
import com.worldpay.access.checkout.cvv.SessionResponseListenerImpl
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator
import com.worldpay.access.checkout.views.CardCVVText

class CvvFlowFragment : Fragment() {

    private lateinit var cvvText: CardCVVText
    private lateinit var submitBtn: Button

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cvv_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            progressBar = ProgressBar(activity)

            cvvText = view.findViewById(R.id.cvv_flow_text_cvv)
            submitBtn = view.findViewById(R.id.cvv_flow_btn_submit)

            initialiseCardValidation(activity)

            initialisePaymentFlow(activity)
        }

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        toggleFields(!progressBar.isLoading())
        super.onViewStateRestored(savedInstanceState)
    }

    private fun initialisePaymentFlow(activity: FragmentActivity) {
        val accessCheckoutClient = AccessCheckoutCVVClient.init(
            getBaseUrl(),
            getMerchantID(),
            SessionResponseListenerImpl(activity, progressBar),
            activity.applicationContext,
            this
        )

        submitBtn.setOnClickListener {
            val cvv = cvvText.getInsertedText()
            accessCheckoutClient.generateSessionState(cvv)
        }
    }

    private fun initialiseCardValidation(activity: FragmentActivity) {
        val card = AccessCheckoutCVV(cvvText)
        card.cardListener = CvvListenerImpl(activity, card)
        card.cardValidator = AccessCheckoutCardValidator()

        CardConfigurationFactory.getRemoteCardConfiguration(card, getBaseUrl())

        cvvText.cardViewListener = card
    }

    private fun toggleFields(enableFields: Boolean) {
        cvvText.isEnabled = enableFields
        submitBtn.isEnabled = enableFields
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)
}