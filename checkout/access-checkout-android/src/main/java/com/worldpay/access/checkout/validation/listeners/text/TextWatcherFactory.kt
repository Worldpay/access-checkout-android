package com.worldpay.access.checkout.validation.listeners.text

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.ResultHandlerFactory
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewDateValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class TextWatcherFactory(
    private val resultHandlerFactory : ResultHandlerFactory
) {

    private val cvcValidationRuleManager = CVCValidationRuleManager()
    private val panValidator = NewPANValidator()
    private val dateValidator = NewDateValidator()

    fun createPanTextWatcher(cvvEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        return PANTextWatcher(
            cardConfiguration = cardConfiguration,
            panValidator = panValidator,
            cvvEditText =  cvvEditText,
            cvcValidator = CVCValidator(resultHandlerFactory.getCvvValidationResultHandler(), cvcValidationRuleManager),
            panValidationResultHandler = resultHandlerFactory.getPanValidationResultHandler(),
            brandChangedHandler = resultHandlerFactory.getBrandChangedHandler(),
            cvcValidationRuleManager = cvcValidationRuleManager
        )
    }

    fun createExpiryDateTextWatcher(expiryDateEditText: EditText): TextWatcher {
        return ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDateEditText,
            expiryDateValidationResultHandler = resultHandlerFactory.getExpiryDateValidationResultHandler(),
            expiryDateSanitiser = ExpiryDateSanitiser()
        )
    }

    fun createCvvTextWatcher(): TextWatcher {
        val cvcValidator = CVCValidator(
            cvvValidationResultHandler = resultHandlerFactory.getCvvValidationResultHandler(),
            cardValidationRuleProvider = cvcValidationRuleManager
        )

        return CVVTextWatcher(cvcValidator)
    }

}
