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
import com.worldpay.access.checkout.client.validation.config.CvvValidationConfig
import com.worldpay.access.checkout.sample.BuildConfig
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvv.CvvValidationListener
import com.worldpay.access.checkout.sample.cvv.SessionResponseListenerImpl

class CvvFlowFragment : Fragment() {

    private lateinit var cvvText: EditText
    private lateinit var submitBtn: SubmitButton
    private lateinit var progressBar: ProgressBar

    private lateinit var cvvValidationListener : CvvValidationListener

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
            submitBtn = SubmitButton(activity, R.id.cvv_flow_btn_submit)

            cvvText = view.findViewById(R.id.cvv_flow_text_cvv)

            cvvValidationListener = CvvValidationListener(activity)

            initialisePaymentFlow(activity, view)
        }

    }

    override fun onResume() {
        super.onResume()
        if (progressBar.isLoading()) {
            disableFields()
            submitBtn.disable()
        } else {
            initialiseCardValidation(cvvValidationListener)
        }
    }

    private fun initialisePaymentFlow(activity : FragmentActivity, view : View) {
        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(getBaseUrl())
            .merchantId(getMerchantID())
            .sessionResponseListener(SessionResponseListenerImpl(activity, progressBar))
            .context(activity.applicationContext)
            .lifecycleOwner(this)
            .build()

        view.findViewById<Button>(R.id.cvv_flow_btn_submit).setOnClickListener {
            Log.d(javaClass.simpleName, "Started request")
            progressBar.beginLoading()
            disableFields()
            submitBtn.disable()

            val cardDetails = CardDetails.Builder().cvv(cvvText.text.toString()).build()
            accessCheckoutClient.generateSession(cardDetails, listOf(SessionType.PAYMENTS_CVC_SESSION))
        }
    }

    private fun initialiseCardValidation(cvvValidationListener: CvvValidationListener) {
        val cvvValidationConfig = CvvValidationConfig.Builder()
            .cvv(cvvText)
            .validationListener(cvvValidationListener)
            .build()

        AccessCheckoutValidationInitialiser.initialise(cvvValidationConfig)
    }

    private fun disableFields() {
        cvvText.isEnabled = false
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)
}
