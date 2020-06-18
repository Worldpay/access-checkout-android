package com.worldpay.access.checkout.validation.listeners.focus

import android.view.View
import com.worldpay.access.checkout.validation.result.handler.CvvValidationResultHandler

internal class CvcFocusChangeListener(
    private val cvvValidationResultHandler : CvvValidationResultHandler
): View.OnFocusChangeListener {

    override fun onFocusChange(v : View?, hasFocus : Boolean) {
        if (!hasFocus) {
            cvvValidationResultHandler.handleFocusChange()
        }
    }

}
