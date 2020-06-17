package com.worldpay.access.checkout.validation.state

internal interface FieldValidationStateManager {
    fun isAllValid(): Boolean
}

internal interface ExpiryDateFieldValidationStateManager :
    FieldValidationStateManager {
    var expiryDateValidated : Boolean
}

internal interface PanFieldValidationStateManager :
    FieldValidationStateManager {
    var panValidated : Boolean
}

internal interface CvcFieldValidationStateManager :
    FieldValidationStateManager {
    var cvvValidated : Boolean
}
