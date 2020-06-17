package com.worldpay.access.checkout.validation.listeners.text

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutExpiryDateValidationListener
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutValidationListener
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.state.CvcFieldValidationStateManager
import com.worldpay.access.checkout.validation.state.ExpiryDateFieldValidationStateManager
import com.worldpay.access.checkout.validation.state.FieldValidationStateManager
import com.worldpay.access.checkout.validation.state.PanFieldValidationStateManager
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewDateValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class TextWatcherFactory(
    private val accessCheckoutValidationListener: AccessCheckoutValidationListener,
    private val validationStateManager: FieldValidationStateManager
) {

    private val cvcValidationRuleManager = CVCValidationRuleManager()
    private val panValidator = NewPANValidator()
    private val dateValidator = NewDateValidator()

    fun createPanTextWatcher(cvvEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        val panValidationResultHandler = PanValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutPanValidationListener,
            validationStateManager = validationStateManager as PanFieldValidationStateManager
        )

        val cvvValidationResultHandler = CvvValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutCvvValidationListener,
            validationStateManager = validationStateManager as CvcFieldValidationStateManager
        )

        return PANTextWatcher(
            cardConfiguration = cardConfiguration,
            panValidator = panValidator,
            cvvEditText =  cvvEditText,
            cvcValidator = CVCValidator(cvvValidationResultHandler, cvcValidationRuleManager),
            panValidationResultHandler = panValidationResultHandler,
            cvcValidationRuleManager = cvcValidationRuleManager
        )
    }

    fun createExpiryDateTextWatcher(expiryDateEditText: EditText): TextWatcher {
        val expiryDateValidationResultHandler = ExpiryDateValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidationListener,
            validationStateManager = validationStateManager as ExpiryDateFieldValidationStateManager
        )

        return ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDateEditText,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler,
            expiryDateSanitiser = ExpiryDateSanitiser()
        )
    }

    fun createCvvTextWatcher(): TextWatcher {
        val cvvValidationResultHandler = CvvValidationResultHandler(
            validationListener = accessCheckoutValidationListener as AccessCheckoutCvvValidationListener,
            validationStateManager = validationStateManager as CvcFieldValidationStateManager
        )

        val cvcValidator = CVCValidator(
            cvvValidationResultHandler = cvvValidationResultHandler,
            cardValidationRuleProvider = cvcValidationRuleManager
        )

        return CVVTextWatcher(cvcValidator)
    }

}
