package com.worldpay.access.checkout.validation.state

internal interface FieldValidationStateManager {
    fun isAllValid(): Boolean
}

internal interface ExpiryDateFieldValidationStateManager : FieldValidationStateManager {
    var expiryDateValidationState : Boolean
}

internal interface PanFieldValidationStateManager : FieldValidationStateManager {
    var panValidationState : Boolean
}

internal interface CvcFieldValidationStateManager : FieldValidationStateManager {
    var cvcValidationState : Boolean
}
