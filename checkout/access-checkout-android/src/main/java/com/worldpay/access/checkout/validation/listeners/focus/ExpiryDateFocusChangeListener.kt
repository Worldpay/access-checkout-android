package com.worldpay.access.checkout.validation.listeners.focus

import android.view.View
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler

internal class ExpiryDateFocusChangeListener(
    private val expiryDateValidationResultHandler : ExpiryDateValidationResultHandler
): View.OnFocusChangeListener {

    override fun onFocusChange(v : View?, hasFocus : Boolean) {
        if (!hasFocus) {
            expiryDateValidationResultHandler.handleFocusChange()
        }
    }

}
