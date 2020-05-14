package com.worldpay.access.checkout.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.AccessCheckoutCVV
import com.worldpay.access.checkout.api.configuration.CardConfigurationFactory
import com.worldpay.access.checkout.client.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.sample.BuildConfig
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvv.CvvListenerImpl
import com.worldpay.access.checkout.sample.cvv.SessionResponseListenerImpl
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
            val cardDetails = CardDetails.Builder().cvv(cvvText.getInsertedText()).build()
            accessCheckoutClient.generateSession(cardDetails, listOf(SessionType.PAYMENTS_CVC_SESSION))
        }
    }

    private fun initialiseCardValidation(activity: FragmentActivity) {
        val card = AccessCheckoutCVV(cvvText)
        card.cardListener = CvvListenerImpl(activity, card)

        CardConfigurationFactory.getRemoteCardConfiguration(card, getBaseUrl())

        cvvText.cardViewListener = card
    }

    private fun toggleFields(enableFields: Boolean) {
        cvvText.isEnabled = enableFields
        submitBtn.isEnabled = false
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)
}