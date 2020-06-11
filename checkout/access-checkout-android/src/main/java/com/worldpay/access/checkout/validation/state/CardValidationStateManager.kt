package com.worldpay.access.checkout.validation.state

class CardValidationStateManager: ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override var panValidated = false
    override var expiryDateValidated = false
    override var cvvValidated = false

    override fun isAllValid(): Boolean {
        return panValidated && expiryDateValidated && cvvValidated
    }

}
