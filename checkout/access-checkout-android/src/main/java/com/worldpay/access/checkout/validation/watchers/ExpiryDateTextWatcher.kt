package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator

internal class ExpiryDateTextWatcher(
    private val dateValidator: NewDateValidator,
    private val expiryDateEditText: EditText,
    private val expiryDateValidationResultHandler: ExpiryDateValidationResultHandler,
    private val expiryDateSanitiser: ExpiryDateSanitiser
): AbstractCardDetailTextWatcher() {

    private var charactersLengthBeforeChange = 0

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        charactersLengthBeforeChange = s.toString().length
        super.beforeTextChanged(s, start, count, after)
    }

    override fun afterTextChanged(editable: Editable?) {
        val expiryDate = editable.toString()
        val isDeletingCharacters = charactersLengthBeforeChange > expiryDate.length

        if (expiryDate.isNotBlank() && !isDeletingCharacters) {
            val newExpiryDate = expiryDateSanitiser.sanitise(expiryDate)

            if (expiryDate != newExpiryDate) {
                expiryDateEditText.setText(newExpiryDate)
                expiryDateEditText.setSelection(newExpiryDate.length)
                return
            }
        }

        val result = dateValidator.validate(editable.toString())
        expiryDateValidationResultHandler.handleResult(isValid = result)
    }

}
