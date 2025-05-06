package com.worldpay.access.checkout.validation.listeners.focus

import android.view.View
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import com.worldpay.access.checkout.validation.result.handler.ExpiryDateValidationResultHandler

internal class ExpiryDateFocusChangeListener(
    private val expiryDateValidationResultHandler: ExpiryDateValidationResultHandler
) : View.OnFocusChangeListener {

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            expiryDateValidationResultHandler.handleFocusChange()
        }
        //Notify AccessCheckoutEditText onFocusListener if it was defined
        (v?.parent as? AccessCheckoutEditText)?.onFocusChangeListener?.onFocusChange(v.parent as AccessCheckoutEditText, hasFocus)
    }
}
