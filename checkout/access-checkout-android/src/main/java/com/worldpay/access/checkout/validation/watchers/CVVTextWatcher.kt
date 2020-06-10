package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.validation.validators.CVCValidator

internal class CVVTextWatcher(
    private val cvcValidator: CVCValidator
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(cvc: Editable) {
        cvcValidator.validate(cvc.toString())
    }

}
