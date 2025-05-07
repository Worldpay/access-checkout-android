package com.worldpay.access.checkout.validation.listeners.focus

import android.view.View
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler

internal class PanFocusChangeListener(
    private val panValidationResultHandler: PanValidationResultHandler
) : View.OnFocusChangeListener {

    override fun onFocusChange(v: View?, hasFocus: Boolean) {

        if (!hasFocus) {
            panValidationResultHandler.handleFocusChange()
        }
        //Notify AccessCheckoutEditText onFocusListener if it was defined
        (v?.parent as? AccessCheckoutEditText)?.onFocusChangeListener?.onFocusChange(v.parent as AccessCheckoutEditText, hasFocus)
    }
}
