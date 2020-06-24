package com.worldpay.access.checkout.validation.result.state

internal object CardValidationStateManager: ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override val panValidationState = FieldValidationState()
    override val expiryDateValidationState = FieldValidationState()
    override val cvcValidationState = FieldValidationState()

    override fun isAllValid(): Boolean {
        return panValidationState.validationState && expiryDateValidationState.validationState && cvcValidationState.validationState
    }

}


