package com.worldpay.access.checkout.validation.watchers

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.AccessCheckoutCvvValidatedSuccessListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutExpiryDateValidatedSuccessListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutPanValidatedSuccessListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationListener
import com.worldpay.access.checkout.validation.result.*
import com.worldpay.access.checkout.validation.validators.CVVValidator
import com.worldpay.access.checkout.validation.validators.DateValidator
import com.worldpay.access.checkout.validation.validators.PANValidator

class TextWatcherFactory(
    private val accessCheckoutValidationListener: AccessCheckoutValidationListener
) {

    private val panValidator = PANValidator()
    private val dateValidator = DateValidator()
    private val cvvValidator = CVVValidator()
    private val validationStateManager = ValidationStateManager()

    fun createPanTextWatcher(panEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        val panValidationResultHandler = PanValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutPanValidatedSuccessListener,
            validationStateManager = validationStateManager
        )

        return PANTextWatcher(
            cardConfiguration = cardConfiguration,
            panValidator = panValidator,
            panEditText = panEditText,
            panValidationResultHandler = panValidationResultHandler
        )
    }

    fun createExpiryMonthTextWatcher(expiryMonthEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        val expiryMonthValidationResultHandler = ExpiryMonthValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidatedSuccessListener,
            validationStateManager = validationStateManager
        )

        return ExpiryMonthTextWatcher(
            cardConfiguration = cardConfiguration,
            dateValidator = dateValidator,
            expiryMonthValidationResultHandler = expiryMonthValidationResultHandler,
            expiryMonthEditText = expiryMonthEditText
        )
    }

    fun createExpiryYearTextWatcher(expiryYearEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        val expiryYearValidationResultHandler = ExpiryYearValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidatedSuccessListener,
            validationStateManager = validationStateManager
        )

        return ExpiryYearTextWatcher(
            cardConfiguration = cardConfiguration,
            dateValidator = dateValidator,
            expiryYearValidationResultHandler = expiryYearValidationResultHandler,
            expiryYearEditText = expiryYearEditText
        )
    }

    fun createCvvTextWatcher(cvvEditText: EditText, panEditText: EditText?, cardConfiguration: CardConfiguration): TextWatcher {
        val cvvValidationResultHandler = CvvValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutCvvValidatedSuccessListener,
            validationStateManager = validationStateManager
        )

        return CVVTextWatcher(
            cardConfiguration = cardConfiguration,
            panEditText = panEditText,
            cvvEditText = cvvEditText,
            cvvValidator = cvvValidator,
            cvvValidationResultHandler = cvvValidationResultHandler
        )
    }

}