package com.worldpay.access.checkout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.logging.LoggingUtils.Companion.debugLog
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.views.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity(), CardListener, SessionResponseListener {

    private lateinit var card: Card
    private lateinit var panView: PANLayout
    private lateinit var cvvText: CardCVVText
    private lateinit var dateText: CardExpiryTextLayout

    private var loading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        panView = findViewById(R.id.panView)
        cvvText = findViewById(R.id.cardCVVText)
        dateText = findViewById(R.id.cardExpiryText)

        card = AccessCheckoutCard(this, panView, cvvText, dateText)
        card.cardListener = this

        panView.cardViewListener = card
        cvvText.cardViewListener = card
        dateText.cardViewListener = card

        val accessCheckoutClient = AccessCheckoutClient.init(
            getBaseUrl(),
            getMerchantID(),
            this,
            applicationContext,
            this
        )

        submit.setOnClickListener {
            val pan = panView.getInsertedText()
            val month = dateText.getMonth()
            val year = dateText.getYear()
            val cvv = cvvText.getInsertedText()
            accessCheckoutClient.generateSessionState(pan, month, year, cvv)
        }
    }

    override fun onRequestStarted() {
        debugLog("MainActivity", "Started request")
        loading = true
        toggleLoading(false)
    }

    override fun onRequestFinished(sessionReference: String?, error: AccessCheckoutException?) {
        debugLog("MainActivity", "Received session reference: $sessionReference")
        loading = false
        toggleLoading(true)
        val toastMessage : String
        if (!sessionReference.isNullOrBlank()){
            toastMessage = "Ref: $sessionReference"
            resetFields()
        }
        else {
            toastMessage = "Error: " + error?.message
        }


        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
    }

    override fun onUpdate(cardView: CardView, valid: Boolean) {
        cardView.isValid(valid)
        submit.isEnabled = card.isValid() && !loading
    }

    override fun onUpdateLengthFilter(cardView: CardView, inputFilter: InputFilter) {
        cardView.applyLengthFilter(inputFilter)
    }

    override fun onUpdateCardBrand(cardBrand: CardBrand?) {
        panView.applyCardLogo(cardBrand?.image ?: "card_unknown")
    }

    private fun fieldsToggle(enableFields: Boolean) {
        if (!enableFields) {
            content_layout.alpha = 0.5f
            loading_bar.visibility = View.VISIBLE
        } else {
            loading_bar.visibility = View.INVISIBLE
            content_layout.alpha = 1.0f
        }
    }

    private fun toggleLoading(enableFields: Boolean) {
        panView.mEditText.isEnabled = enableFields
        cardCVVText.isEnabled = enableFields
        cardExpiryText.monthEditText.isEnabled = enableFields
        cardExpiryText.yearEditText.isEnabled = enableFields
        submit.isEnabled = enableFields

        fieldsToggle(enableFields)
    }

    private fun resetFields() {
        panView.mEditText.text.clear()
        cardCVVText.text.clear()
        cardExpiryText.monthEditText.text.clear()
        cardExpiryText.yearEditText.text.clear()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean("loading", loading)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            loading = savedInstanceState.getBoolean("loading")

            if (loading)
                toggleLoading(false)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun getMerchantID() = getString(R.string.merchantId)

    private fun getBaseUrl() = getString(R.string.endpoint)
}