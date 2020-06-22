package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import com.worldpay.access.checkout.validation.validators.CVCValidator

internal class CVCTextWatcher(
    private val cvcValidator: CVCValidator
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(cvc: Editable) {
        cvcValidator.validate(cvc.toString())
    }

}