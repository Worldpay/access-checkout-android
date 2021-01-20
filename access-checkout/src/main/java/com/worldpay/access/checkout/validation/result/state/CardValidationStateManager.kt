package com.worldpay.access.checkout.validation.result.state

internal class CardValidationStateManager: ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override val panValidationState = FieldValidationState()
    override val expiryDateValidationState = FieldValidationState()
    override val cvcValidationState = FieldValidationState()

    override fun isAllValid(): Boolean {
        return panValidationState.validationState && expiryDateValidationState.validationState && cvcValidationState.validationState
    }

}


