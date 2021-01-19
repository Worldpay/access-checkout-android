package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.worldpay.access.checkout.client.validation.model.CardBrands
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.ExpiryDateValidator
import com.worldpay.access.checkout.validation.validators.PanValidator

internal class TextWatcherFactory(
    private val resultHandlerFactory : ResultHandlerFactory
) {

    private val cvcValidationRuleManager = CVCValidationRuleManager()
    private val dateValidator = ExpiryDateValidator()

    fun createPanTextWatcher(cvcEditText: EditText, acceptedCardBrands: Array<CardBrands>): PanTextWatcher {
        return PanTextWatcher(
            panValidator = PanValidator(acceptedCardBrands),
            cvcEditText =  cvcEditText,
            cvcValidator = CvcValidator(resultHandlerFactory.getCvcValidationResultHandler(), cvcValidationRuleManager),
            panValidationResultHandler = resultHandlerFactory.getPanValidationResultHandler(),
            brandChangedHandler = resultHandlerFactory.getBrandChangedHandler(),
            cvcValidationRuleManager = cvcValidationRuleManager
        )
    }

    fun createExpiryDateTextWatcher(expiryDateEditText: EditText): ExpiryDateTextWatcher {
        return ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDateEditText,
            expiryDateValidationResultHandler = resultHandlerFactory.getExpiryDateValidationResultHandler(),
            expiryDateSanitiser = ExpiryDateSanitiser()
        )
    }

    fun createCvcTextWatcher(): CvcTextWatcher {
        val cvcValidator = CvcValidator(
            cvcValidationResultHandler = resultHandlerFactory.getCvcValidationResultHandler(),
            cardValidationRuleProvider = cvcValidationRuleManager
        )

        return CvcTextWatcher(cvcValidator)
    }

}
