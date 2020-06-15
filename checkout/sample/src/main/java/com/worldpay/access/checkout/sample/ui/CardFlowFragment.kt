package com.worldpay.access.checkout.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.session.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.session.model.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.sample.BuildConfig
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.CardValidationListener
import com.worldpay.access.checkout.sample.card.SessionResponseListenerImpl
import com.worldpay.access.checkout.util.logging.LoggingUtils
import kotlin.properties.Delegates

class CardFlowFragment : Fragment() {

    private var submitBtnEnabledColor by Delegates.notNull<Int>()
    private var submitBtnDisabledColor by Delegates.notNull<Int>()

    private lateinit var panText: EditText
    private lateinit var cvvText: EditText
    private lateinit var expiryText: EditText
    private lateinit var submitBtn: Button
    private lateinit var paymentsCvcSwitch: Switch

    private lateinit var progressBar: ProgressBar

    private lateinit var sessionTypes: List<SessionType>

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

            panText = view.findViewById(R.id.card_flow_text_pan)
            expiryText = view.findViewById(R.id.card_flow_expiry_date)
            cvvText = view.findViewById(R.id.card_flow_text_cvv)
            submitBtn = view.findViewById(R.id.card_flow_btn_submit)
            paymentsCvcSwitch = view.findViewById(R.id.card_flow_payments_cvc_switch)

            submitBtnEnabledColor =
                getColor(activity.resources, R.color.colorPrimary, null)

            submitBtnDisabledColor =
                getColor(activity.resources, android.R.color.darker_gray, null)

            handleSwitch()

            initialiseCardValidation(activity)

            initialisePaymentFlow(activity)
        }
    }

    override fun onResume() {
        toggleFields(!progressBar.isLoading())
        super.onResume()
    }

    private fun handleSwitch() {
        setSessionTypes(paymentsCvcSwitch.isChecked)
        paymentsCvcSwitch.setOnCheckedChangeListener { _, isChecked ->
            setSessionTypes(isChecked)
        }
    }

    private fun setSessionTypes(isChecked: Boolean) {
        sessionTypes = if (isChecked) {
            listOf(VERIFIED_TOKEN_SESSION, PAYMENTS_CVC_SESSION)
        } else {
            listOf(VERIFIED_TOKEN_SESSION)
        }
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
            LoggingUtils.debugLog(javaClass.simpleName, "Started request")
            this.progressBar.beginLoading()
            toggleFields(false)

            val cardDetails = CardDetails.Builder()
                .pan(panText.text.toString())
                .expiryDate(expiryText.text.toString())
                .cvv(cvvText.text.toString())
                .build()

            accessCheckoutClient.generateSession(cardDetails, sessionTypes)
        }
    }

    private fun initialiseCardValidation(activity: FragmentActivity) {
        val cardValidationListener = CardValidationListener(activity)

        val cardValidationConfig = CardValidationConfig.Builder()
            .baseUrl(getBaseUrl())
            .pan(panText)
            .expiryDate(expiryText)
            .cvv(cvvText)
            .validationListener(cardValidationListener)
            .build()

        AccessCheckoutValidationInitialiser.initialise(cardValidationConfig)
    }

    private fun toggleFields(enableFields: Boolean) {
        panText.isEnabled = enableFields
        cvvText.isEnabled = enableFields
        expiryText.isEnabled = enableFields
        paymentsCvcSwitch.isEnabled = enableFields
        submitBtn.isEnabled = false
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)

}
