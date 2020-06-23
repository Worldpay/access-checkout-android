package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import com.worldpay.access.checkout.validation.validators.CvcValidator

internal class CvcTextWatcher(
    private val cvcValidator: CvcValidator
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(cvc: Editable) {
        cvcValidator.validate(cvc.toString())
    }

}
