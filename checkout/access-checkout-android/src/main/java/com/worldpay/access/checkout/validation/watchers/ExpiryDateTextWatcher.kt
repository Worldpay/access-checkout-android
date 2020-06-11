package com.worldpay.access.checkout.validation.watchers

import android.annotation.SuppressLint
import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator

internal class ExpiryDateTextWatcher(
    private val dateValidator: NewDateValidator,
    private val expiryDateEditText: EditText,
    private val expiryDateValidationResultHandler: ExpiryDateValidationResultHandler
): AbstractCardDetailTextWatcher() {

    @SuppressLint("SetTextI18n")
    override fun afterTextChanged(expiryDate: Editable?) {

        if (expiryDate.toString().length == 2) {
            expiryDateEditText.setText("${expiryDate.toString()}/")
            return
        }

        val result = dateValidator.validate(expiryDate.toString())
        expiryDateValidationResultHandler.handleResult(isValid = result)
    }

}
