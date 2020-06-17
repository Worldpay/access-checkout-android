package com.worldpay.access.checkout.validation.state

internal class CardValidationStateManager: ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override var panValidationState = false
    override var expiryDateValidationState = false
    override var cvvValidated = false

    override fun isAllValid(): Boolean {
        return panValidationState && expiryDateValidationState && cvvValidated
    }

}
