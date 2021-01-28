package com.worldpay.access.checkout.validation.result.state

internal interface FieldValidationStateManager {
    fun isAllValid(): Boolean
}

internal interface ExpiryDateFieldValidationStateManager : FieldValidationStateManager {
    val expiryDateValidationState : FieldValidationState
}

internal interface PanFieldValidationStateManager : FieldValidationStateManager {
    val panValidationState : FieldValidationState
}

internal interface CvcFieldValidationStateManager : FieldValidationStateManager {
    val cvcValidationState : FieldValidationState
}

internal class FieldValidationState(val id: Int) {
    var validationState = false
    var notificationSent = false
}
