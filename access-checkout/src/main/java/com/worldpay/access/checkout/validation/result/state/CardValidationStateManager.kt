package com.worldpay.access.checkout.validation.result.state

import android.widget.EditText

internal class CardValidationStateManager(
    pan: EditText,
    expiryDate: EditText,
    cvc: EditText
) : ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override var panValidationState = FieldValidationState(pan.id)
    override var expiryDateValidationState = FieldValidationState(expiryDate.id)
    override var cvcValidationState = FieldValidationState(cvc.id)

    override fun isAllValid(): Boolean {
        return panValidationState.validationState && expiryDateValidationState.validationState && cvcValidationState.validationState
    }

}


