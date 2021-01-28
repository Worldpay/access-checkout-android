package com.worldpay.access.checkout.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.sample.BuildConfig
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.RestrictedCardValidationListener
import com.worldpay.access.checkout.sample.images.SVGImageLoader

class RestrictedCardFlowFragment : Fragment() {

    private lateinit var panText: EditText
    private lateinit var cvcText: EditText
    private lateinit var expiryText: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var cardValidationListener : AccessCheckoutCardValidationListener

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

    private fun initialiseCardValidation(cardValidationListener : AccessCheckoutCardValidationListener) {
        val cardValidationConfig = CardValidationConfig.Builder()
            .baseUrl(getBaseUrl())
            .pan(panText)
            .expiryDate(expiryText)
            .cvc(cvcText)
            .acceptedCardBrands(arrayOf("visa", "mastercard", "amex"))
            .validationListener(cardValidationListener)
            .lifecycleOwner(this)
            .build()

        AccessCheckoutValidationInitialiser.initialise(cardValidationConfig)
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)

}
