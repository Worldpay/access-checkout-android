package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.ExpiryYearValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryYearTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val expiryYearValidationResultHandler: ExpiryYearValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(year: Editable?) {
        val result = dateValidator.validate(null, year.toString(), cardConfiguration)
        expiryYearValidationResultHandler.handleResult(
            validationResult = result
        )
    }

}
