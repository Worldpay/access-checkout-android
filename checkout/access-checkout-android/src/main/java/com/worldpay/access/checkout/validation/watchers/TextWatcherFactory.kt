package com.worldpay.access.checkout.validation.watchers

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationListener
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.result.ValidationStateManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewDateValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class TextWatcherFactory(
    private val accessCheckoutValidationListener: AccessCheckoutValidationListener
) {

    private val validationStateManager = ValidationStateManager()

    private val panValidator = NewPANValidator()
    private val dateValidator = NewDateValidator()

    fun createPanTextWatcher(cvvEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        val panValidationResultHandler = PanValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutPanValidationListener,
            validationStateManager = validationStateManager
        )

        val cvvValidationResultHandler = CvvValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutCvvValidationListener,
            validationStateManager = validationStateManager
        )

        return PANTextWatcher(
            cardConfiguration = cardConfiguration,
            panValidator = panValidator,
            cvvEditText =  cvvEditText,
            cvcValidator = CVCValidator(cvvValidationResultHandler),
            panValidationResultHandler = panValidationResultHandler
        )
    }

    fun createExpiryMonthTextWatcher(yearEditText: EditText): TextWatcher {
        val expiryDateValidationResultHandler = ExpiryDateValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidationListener,
            validationStateManager = validationStateManager
        )

        return ExpiryMonthTextWatcher(
            dateValidator = dateValidator,
            yearEditText = yearEditText,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )
    }

    fun createExpiryYearTextWatcher(monthEditText: EditText): TextWatcher {
        val expiryDateValidationResultHandler = ExpiryDateValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidationListener,
            validationStateManager = validationStateManager
        )

        return ExpiryYearTextWatcher(
            dateValidator = dateValidator,
            monthEditText = monthEditText,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )
    }

    fun createCvvTextWatcher(): TextWatcher {
        val cvvValidationResultHandler = CvvValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutCvvValidationListener,
            validationStateManager = validationStateManager
        )

        val cvcValidator = CVCValidator(cvvValidationResultHandler)

        return CVVTextWatcher(cvcValidator)
    }

}
