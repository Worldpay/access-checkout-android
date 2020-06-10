package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator

internal class ExpiryMonthTextWatcher(
    private val dateValidator: NewDateValidator,
    private val yearEditText: EditText,
    private val expiryDateValidationResultHandler: ExpiryDateValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(month: Editable?) {
        val year = yearEditText.text.toString()

        if (year.isNotBlank()) {
            val result = dateValidator.validate(month.toString(), year)
            expiryDateValidationResultHandler.handleResult(isValid = result)
        }
    }

}
