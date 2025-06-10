package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.worldpay.access.checkout.cardbin.api.service.CardBinService
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
        panEditText: EditText,
        cvcEditText: EditText,
        acceptedCardBrands: Array<String>,
        enablePanFormatting: Boolean,
        checkoutId: String
    ): PanTextWatcher {
        return PanTextWatcher(
            panEditText = panEditText,
            panValidator = PanValidator(acceptedCardBrands),
            panFormatter = PanFormatter(enablePanFormatting),
            cvcValidator = CvcValidator(
                resultHandlerFactory.getCvcValidationResultHandler(),
                cvcValidationRuleManager
            ),
            cvcAccessEditText = cvcEditText,
            panValidationResultHandler = resultHandlerFactory.getPanValidationResultHandler(),
            brandsChangedHandler = resultHandlerFactory.getBrandsChangedHandler(),
            cvcValidationRuleManager = cvcValidationRuleManager,
            cardBinService = CardBinService(
                checkoutId = checkoutId,
                baseUrl = "https://localhost:3003",
            )
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
