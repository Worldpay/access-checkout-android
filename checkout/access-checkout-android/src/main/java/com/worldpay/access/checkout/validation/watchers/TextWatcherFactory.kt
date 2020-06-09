package com.worldpay.access.checkout.validation.watchers

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationListener
import com.worldpay.access.checkout.validation.result.*
import com.worldpay.access.checkout.validation.validators.CVVValidator
import com.worldpay.access.checkout.validation.validators.DateValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator

class TextWatcherFactory(
    private val accessCheckoutValidationListener: AccessCheckoutValidationListener
) {

    private val panValidator = NewPANValidator()
    private val dateValidator = DateValidator()
    private val cvvValidator = CVVValidator()
    private val validationStateManager = ValidationStateManager()

    fun createPanTextWatcher(cardConfiguration: CardConfiguration): TextWatcher {
        val panValidationResultHandler = PanValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutPanValidationListener,
            validationStateManager = validationStateManager
        )

        return PANTextWatcher(
            cardConfiguration = cardConfiguration,
            panValidator = panValidator,
            panValidationResultHandler = panValidationResultHandler
        )
    }

    fun createExpiryMonthTextWatcher(cardConfiguration: CardConfiguration): TextWatcher {
        val expiryMonthValidationResultHandler = ExpiryMonthValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidationListener,
            validationStateManager = validationStateManager
        )

        return ExpiryMonthTextWatcher(
            cardConfiguration = cardConfiguration,
            dateValidator = dateValidator,
            expiryMonthValidationResultHandler = expiryMonthValidationResultHandler
        )
    }

    fun createExpiryYearTextWatcher(cardConfiguration: CardConfiguration): TextWatcher {
        val expiryYearValidationResultHandler = ExpiryYearValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidationListener,
            validationStateManager = validationStateManager
        )

        return ExpiryYearTextWatcher(
            cardConfiguration = cardConfiguration,
            dateValidator = dateValidator,
            expiryYearValidationResultHandler = expiryYearValidationResultHandler
        )
    }

    fun createCvvTextWatcher(panEditText: EditText?, cardConfiguration: CardConfiguration): TextWatcher {
        val cvvValidationResultHandler = CvvValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutCvvValidationListener,
            validationStateManager = validationStateManager
        )

        return CVVTextWatcher(
            cardConfiguration = cardConfiguration,
            panEditText = panEditText,
            cvvValidator = cvvValidator,
            cvvValidationResultHandler = cvvValidationResultHandler
        )
    }

}
