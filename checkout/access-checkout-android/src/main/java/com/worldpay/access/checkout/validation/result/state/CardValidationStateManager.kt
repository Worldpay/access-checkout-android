package com.worldpay.access.checkout.validation.result.state

internal object CardValidationStateManager: ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override var panValidationState = FieldValidationState()
    override var expiryDateValidationState = FieldValidationState()
    override var cvcValidationState = FieldValidationState()

    override fun isAllValid(): Boolean {
        return panValidationState.validationState && expiryDateValidationState.validationState && cvcValidationState.validationState
    }

}


