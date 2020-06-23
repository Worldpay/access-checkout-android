package com.worldpay.access.checkout.validation.listeners.text

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
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

    fun createPanTextWatcher(cvcEditText: EditText): TextWatcher {
        return PANTextWatcher(
            panValidator = panValidator,
            cvcEditText =  cvcEditText,
            cvcValidator = CVCValidator(resultHandlerFactory.getCvcValidationResultHandler(), cvcValidationRuleManager),
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

    fun createCvcTextWatcher(): TextWatcher {
        val cvcValidator = CVCValidator(
            cvcValidationResultHandler = resultHandlerFactory.getCvcValidationResultHandler(),
            cardValidationRuleProvider = cvcValidationRuleManager
        )

        return CVCTextWatcher(cvcValidator)
    }

}
