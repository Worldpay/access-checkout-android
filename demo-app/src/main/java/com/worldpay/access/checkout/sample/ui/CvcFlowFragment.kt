package com.worldpay.access.checkout.sample.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worldpay.access.checkout.client.session.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CvcValidationConfig
import com.worldpay.access.checkout.sample.BuildConfig
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvc.CvcValidationListener
import com.worldpay.access.checkout.sample.cvc.SessionResponseListenerImpl

class CvcFlowFragment : Fragment() {

    private lateinit var cvcText: EditText
    private lateinit var submitBtn: SubmitButton
    private lateinit var progressBar: ProgressBar

    private lateinit var cvcValidationListener: CvcValidationListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cvc_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            progressBar = ProgressBar(activity)
            submitBtn = SubmitButton(activity, R.id.cvc_flow_btn_submit)

            cvcText = view.findViewById(R.id.cvc_flow_text_cvc)

            cvcValidationListener = CvcValidationListener(activity)

            initialisePaymentFlow(activity, view)
        }
    }

    override fun onResume() {
        super.onResume()
        if (progressBar.isLoading()) {
            disableFields()
            submitBtn.disable()
        } else {
            initialiseCardValidation(cvcValidationListener)
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

        view.findViewById<Button>(R.id.cvc_flow_btn_submit).setOnClickListener {
            Log.d(javaClass.simpleName, "Started request")
            progressBar.beginLoading()
            disableFields()
            submitBtn.disable()

            val cardDetails = CardDetails.Builder().cvc(cvcText.text.toString()).build()
            accessCheckoutClient.generateSessions(cardDetails, listOf(SessionType.CVC))
        }
    }

    private fun initialiseCardValidation(cvcValidationListener: CvcValidationListener) {
        val cvcValidationConfig = CvcValidationConfig.Builder()
            .cvc(cvcText)
            .validationListener(cvcValidationListener)
            .lifecycleOwner(this)
            .build()

        AccessCheckoutValidationInitialiser.initialise(cvcValidationConfig)
    }

    private fun disableFields() {
        cvcText.isEnabled = false
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)
}
