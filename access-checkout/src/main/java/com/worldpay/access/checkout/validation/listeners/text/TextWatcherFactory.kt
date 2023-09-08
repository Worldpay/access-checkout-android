package com.worldpay.access.checkout.validation.listeners.text

import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.formatter.PanFormatter
import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.ExpiryDateValidator
import com.worldpay.access.checkout.validation.validators.PanValidator

internal class TextWatcherFactory(
    private val resultHandlerFactory: ResultHandlerFactory
) {

    private val cvcValidationRuleManager = CVCValidationRuleManager()
    private val dateValidator = ExpiryDateValidator()

    fun createPanTextWatcher(
        panAccessEditText: AccessEditText,
        cvcAccessEditText: AccessEditText,
        acceptedCardBrands: Array<String>,
        enablePanFormatting: Boolean
    ): PanTextWatcher {
        return PanTextWatcher(
            panAccessEditText = panAccessEditText,
            panValidator = PanValidator(acceptedCardBrands),
            panFormatter = PanFormatter(enablePanFormatting),
            cvcValidator = CvcValidator(resultHandlerFactory.getCvcValidationResultHandler(), cvcValidationRuleManager),
            cvcAccessEditText = cvcAccessEditText,
            panValidationResultHandler = resultHandlerFactory.getPanValidationResultHandler(),
            brandChangedHandler = resultHandlerFactory.getBrandChangedHandler(),
            cvcValidationRuleManager = cvcValidationRuleManager
        )
    }

    fun createExpiryDateTextWatcher(expiryDateAccessEditText: AccessEditText): ExpiryDateTextWatcher {
        return ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateAccessEditText = expiryDateAccessEditText,
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
