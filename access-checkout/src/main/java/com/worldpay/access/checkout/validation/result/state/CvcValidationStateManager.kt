package com.worldpay.access.checkout.validation.result.state

import android.widget.EditText

internal class CvcValidationStateManager(cvc: EditText) : CvcFieldValidationStateManager {

    override val cvcValidationState = FieldValidationState(cvc.id)

    override fun isAllValid(): Boolean {
        return cvcValidationState.validationState
    }
}
