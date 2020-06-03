package com.worldpay.access.checkout.client

import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull
import com.worldpay.access.checkout.validation.AccessCheckoutValidationController
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

class AccessCheckoutValidationInitialiser {

    private var pan: EditText? = null
    private var expiryMonth: EditText? = null
    private var expiryYear: EditText? = null
    private var cvv: EditText? = null
    private var baseUrl: String? = null
    private var validationListener: AccessCheckoutValidationListener? = null

    private val client = CardConfigurationClientFactory.createClient()

    fun pan(pan: EditText): AccessCheckoutValidationInitialiser {
        this.pan = pan
        return this
    }

    fun expiryMonth(expiryMonth: EditText): AccessCheckoutValidationInitialiser {
        this.expiryMonth = expiryMonth
        return this
    }

    fun expiryYear(expiryYear: EditText): AccessCheckoutValidationInitialiser {
        this.expiryYear = expiryYear
        return this
    }

    fun cvv(cvv: EditText): AccessCheckoutValidationInitialiser {
        this.cvv = cvv
        return this
    }

    fun baseUrl(baseURL: String): AccessCheckoutValidationInitialiser {
        this.baseUrl = baseURL
        return this
    }

    fun validationListener(validationListener: AccessCheckoutValidationListener): AccessCheckoutValidationInitialiser {
        this.validationListener = validationListener
        return this
    }

    fun initialise() {
        validateNotNull(pan, "pan component")
        validateNotNull(expiryMonth, "expiry month component")
        validateNotNull(expiryYear, "expiry year component")
        validateNotNull(cvv, "cvv component")
        validateNotNull(baseUrl, "base url")
        validateNotNull(validationListener, "validation listener")

        val cardDetailComponents = CardDetailComponents(
            pan = pan as EditText,
            expiryMonth = expiryMonth as EditText,
            expiryYear = expiryYear as EditText,
            cvv = cvv as EditText
        )

        val textWatcherFactory = TextWatcherFactory(
            validationListener = validationListener as AccessCheckoutValidationListener,
            cardDetailComponents = cardDetailComponents
        )

        AccessCheckoutValidationController(
            baseUrl = baseUrl as String,
            cardDetailComponents = cardDetailComponents,
            cardConfigurationClient = client,
            textWatcherFactory = textWatcherFactory
        )
    }

}