package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator

internal class ExpiryDateTextWatcher(
    private val dateValidator: NewDateValidator,
    private val expiryDateValidationResultHandler: ExpiryDateValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(year: Editable?) {
        val result = dateValidator.validate("", year.toString())
        expiryDateValidationResultHandler.handleResult(isValid = result)
    }

}
