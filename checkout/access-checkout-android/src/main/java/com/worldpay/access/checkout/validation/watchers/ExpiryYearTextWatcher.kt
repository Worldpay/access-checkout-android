package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator

internal class ExpiryYearTextWatcher(
    private val dateValidator: NewDateValidator,
    private val monthEditText: EditText,
    private val expiryDateValidationResultHandler: ExpiryDateValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(year: Editable?) {
        val month = monthEditText.text.toString()

        if (month.isNotBlank()) {
            val result = dateValidator.validate(month, year.toString())
            expiryDateValidationResultHandler.handleResult(isValid = result)
        }
    }

}
