package com.worldpay.access.checkout.validation.state

interface FieldValidationStateManager {
    fun isAllValid(): Boolean
}

interface ExpiryDateFieldValidationStateManager :
    FieldValidationStateManager {
    var expiryDateValidated : Boolean
}

interface PanFieldValidationStateManager :
    FieldValidationStateManager {
    var panValidated : Boolean
}

interface CvcFieldValidationStateManager :
    FieldValidationStateManager {
    var cvvValidated : Boolean
}