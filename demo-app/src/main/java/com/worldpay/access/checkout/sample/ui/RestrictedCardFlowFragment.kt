package com.worldpay.access.checkout.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.sample.BuildConfig
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.RestrictedCardValidationListener
import com.worldpay.access.checkout.sample.card.SessionResponseListenerImpl
import com.worldpay.access.checkout.sample.images.SVGImageLoader
import com.worldpay.access.checkout.ui.AccessCheckoutEditText

class RestrictedCardFlowFragment : Fragment() {

    private lateinit var panText: AccessCheckoutEditText
    private lateinit var cvcText: AccessCheckoutEditText
    private lateinit var expiryText: AccessCheckoutEditText
    private lateinit var progressBar: ProgressBar

    private lateinit var cardValidationListener: AccessCheckoutCardValidationListener

    private lateinit var accessCheckoutClient: AccessCheckoutClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restricted_card_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            progressBar = ProgressBar(activity)

            panText = view.findViewById(R.id.restricted_card_flow_text_pan)
            expiryText = view.findViewById(R.id.restricted_card_flow_expiry_date)
            cvcText = view.findViewById(R.id.restricted_card_flow_text_cvc)

            val brandImageView = view.findViewById<ImageView>(R.id.restricted_card_flow_brand_logo)
            SVGImageLoader.getInstance(activity).fetchAndApplyCardLogo(null, brandImageView)

            createAccessCheckoutClient(activity)

            cardValidationListener = RestrictedCardValidationListener(activity)
        }
    }

    override fun onResume() {
        super.onResume()
        if (progressBar.isLoading()) {
            panText.isEnabled = false
        } else {
            initialiseCardValidation(cardValidationListener)
        }
    }

    private fun initialiseCardValidation(
        cardValidationListener: AccessCheckoutCardValidationListener
    ) {
        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(panText)
            .expiryDate(expiryText)
            .cvc(cvcText)
            .acceptedCardBrands(arrayOf("visa", "mastercard", "amex"))
            .validationListener(cardValidationListener)
            .build()

        accessCheckoutClient.initialiseValidation(cardValidationConfig)
    }

    private fun getBaseUrl() = getString(R.string.endpoint)
    private fun getCheckoutId() = BuildConfig.CHECKOUT_ID
    
    private fun createAccessCheckoutClient(activity: FragmentActivity) {
        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(getBaseUrl())
            .checkoutId(getCheckoutId())
            .sessionResponseListener(SessionResponseListenerImpl(activity, progressBar))
            .context(activity.applicationContext)
            .lifecycleOwner(this)
            .build()
    }
}
