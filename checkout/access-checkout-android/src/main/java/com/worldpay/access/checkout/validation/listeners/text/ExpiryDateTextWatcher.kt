package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.validation.listeners.text.ExpiryDateSanitiser.Companion.SEPARATOR
import com.worldpay.access.checkout.validation.result.handler.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator

internal class ExpiryDateTextWatcher(
    private val dateValidator: NewDateValidator,
    private val expiryDateEditText: EditText,
    private val expiryDateValidationResultHandler: ExpiryDateValidationResultHandler,
    private val expiryDateSanitiser: ExpiryDateSanitiser
): AbstractCardDetailTextWatcher() {

    private var textBefore = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        textBefore = s.toString()
        super.beforeTextChanged(s, start, count, after)
    }

    override fun afterTextChanged(editable: Editable?) {
        val expiryDate = editable.toString()

        if (expiryDate.isNotBlank()) {
            val isNotDeletingSeparator = !attemptingToDeleteSeparator(textBefore, expiryDate)
            val hasNoSeparator = !expiryDate.contains(SEPARATOR)

            if (hasNoSeparator && isNotDeletingSeparator) {
                val newExpiryDate = expiryDateSanitiser.sanitise(expiryDate)
                if (expiryDate != newExpiryDate) {
                    updateText(newExpiryDate)
                    return
                }
            }
        }

        val result = dateValidator.validate(expiryDate)
        expiryDateValidationResultHandler.handleResult(isValid = result)
    }

    private fun attemptingToDeleteSeparator(textBefore: String, textAfter: String): Boolean {
        if (textBefore.endsWith(SEPARATOR) && !textAfter.endsWith(SEPARATOR)) {
            return textAfter.plus(SEPARATOR) == textBefore
        }
        return false
    }

    private fun updateText(text: String) {
        expiryDateEditText.setText(text)
        expiryDateEditText.setSelection(text.length)
    }

}
