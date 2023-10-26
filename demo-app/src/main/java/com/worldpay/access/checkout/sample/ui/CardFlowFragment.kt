package com.worldpay.access.checkout.sample.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.session.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.sample.BuildConfig
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.CardValidationListener
import com.worldpay.access.checkout.sample.card.SessionResponseListenerImpl
import com.worldpay.access.checkout.sample.images.SVGImageLoader
import com.worldpay.access.checkout.ui.AccessCheckoutEditText

class CardFlowFragment : Fragment() {

    private lateinit var panText: AccessCheckoutEditText
    private lateinit var cvcText: AccessCheckoutEditText
    private lateinit var expiryText: AccessCheckoutEditText
    private lateinit var submitBtn: SubmitButton
    private lateinit var paymentsCvcSwitch: SwitchCompat
    private lateinit var progressBar: ProgressBar

    private lateinit var sessionTypes: List<SessionType>

    private lateinit var cardValidationListener: CardValidationListener

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
            submitBtn = SubmitButton(activity, R.id.card_flow_btn_submit)

            panText = view.findViewById(R.id.card_flow_text_pan)
            expiryText = view.findViewById(R.id.card_flow_expiry_date)
            cvcText = view.findViewById(R.id.card_flow_text_cvc)
            paymentsCvcSwitch = view.findViewById(R.id.card_flow_payments_cvc_switch)

            val brandImageView = view.findViewById<ImageView>(R.id.card_flow_brand_logo)
            SVGImageLoader.getInstance(activity).fetchAndApplyCardLogo(null, brandImageView)

            handleSwitch()

            cardValidationListener = CardValidationListener(activity)

            initialisePaymentFlow(activity, view)
        }
    }

    override fun onResume() {
        super.onResume()
        if (progressBar.isLoading()) {
            disableFields()
            submitBtn.disable()
        } else {
            initialiseCardValidation(cardValidationListener)
        }
    }

    private fun handleSwitch() {
        setSessionTypes(paymentsCvcSwitch.isChecked)
        paymentsCvcSwitch.setOnCheckedChangeListener { _, isChecked ->
            setSessionTypes(isChecked)
        }
    }

    private fun setSessionTypes(isChecked: Boolean) {
        sessionTypes = if (isChecked) {
            listOf(CARD, CVC)
        } else {
            listOf(CARD)
        }
    }

    private fun initialisePaymentFlow(activity: FragmentActivity, view: View) {
        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(getBaseUrl())
            .merchantId(getMerchantID())
            .sessionResponseListener(SessionResponseListenerImpl(activity, progressBar))
            .context(activity.applicationContext)
            .lifecycleOwner(this)
            .build()

        view.findViewById<Button>(R.id.card_flow_btn_submit).setOnClickListener {
            Log.d(javaClass.simpleName, "Started request")
            this.progressBar.beginLoading()
            disableFields()
            submitBtn.disable()

            val cardDetails = CardDetails.Builder()
                .pan(panText)
                .expiryDate(expiryText)
                .cvc(cvcText)
                .build()

            accessCheckoutClient.generateSessions(cardDetails, sessionTypes)
        }
    }

    private fun initialiseCardValidation(cardValidationListener: CardValidationListener) {
        val cardValidationConfig = CardValidationConfig.Builder()
            .baseUrl(getBaseUrl())
            .pan(panText)
            .expiryDate(expiryText)
            .cvc(cvcText)
            .validationListener(cardValidationListener)
            .lifecycleOwner(this)
            .enablePanFormatting()
            .build()

        AccessCheckoutValidationInitialiser.initialise(cardValidationConfig)
    }

    private fun disableFields() {
        panText.isEnabled = false
        cvcText.isEnabled = false
        expiryText.isEnabled = false
        paymentsCvcSwitch.isEnabled = false
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)
}
