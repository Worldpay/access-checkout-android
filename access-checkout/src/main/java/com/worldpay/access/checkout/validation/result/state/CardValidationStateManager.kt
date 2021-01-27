package com.worldpay.access.checkout.validation.result.state

import android.widget.EditText

internal class CardValidationStateManager(
    pan: EditText,
    expiryDate: EditText,
    cvc: EditText
) : ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override val panValidationState = FieldValidationState(pan.id)
    override val expiryDateValidationState = FieldValidationState(expiryDate.id)
    override val cvcValidationState = FieldValidationState(cvc.id)

    override fun isAllValid(): Boolean {
        return panValidationState.validationState && expiryDateValidationState.validationState && cvcValidationState.validationState
    }

}


