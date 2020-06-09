package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.ExpiryMonthValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryMonthTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val expiryMonthValidationResultHandler: ExpiryMonthValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(month: Editable?) {
        val result = dateValidator.validate(month.toString(), null, cardConfiguration)
        expiryMonthValidationResultHandler.handleResult(
            validationResult = result
        )
    }

}
