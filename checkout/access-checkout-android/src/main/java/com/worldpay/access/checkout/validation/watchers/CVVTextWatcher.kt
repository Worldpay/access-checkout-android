package com.worldpay.access.checkout.validation.watchers

import android.text.Editable

internal class CVVTextWatcher(
    private val cvcValidationHandler: CVCValidationHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(cvc: Editable) {
        cvcValidationHandler.validate(cvc.toString())
    }

}
