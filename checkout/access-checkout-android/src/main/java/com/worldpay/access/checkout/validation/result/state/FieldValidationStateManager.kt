package com.worldpay.access.checkout.validation.result.state

internal interface FieldValidationStateManager {
    fun isAllValid(): Boolean
}

internal interface ExpiryDateFieldValidationStateManager : FieldValidationStateManager {
    var expiryDateValidationState : FieldValidationState
}

internal interface PanFieldValidationStateManager : FieldValidationStateManager {
    var panValidationState : FieldValidationState
}

internal interface CvcFieldValidationStateManager : FieldValidationStateManager {
    var cvcValidationState : FieldValidationState
}

internal class FieldValidationState {
    var validationState = false
    var notificationSent = false
}
