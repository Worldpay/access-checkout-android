package com.worldpay.access.checkout.validation.listeners.focus

import android.view.View
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler

internal class PanFocusChangeListener(
    private val panValidationResultHandler : PanValidationResultHandler
): View.OnFocusChangeListener {

    override fun onFocusChange(v : View?, hasFocus : Boolean) {
        if (!hasFocus) {
            panValidationResultHandler.handleFocusChange()
        }
    }

}
