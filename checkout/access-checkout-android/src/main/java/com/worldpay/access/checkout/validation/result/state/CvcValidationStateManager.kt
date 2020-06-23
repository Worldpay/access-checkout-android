package com.worldpay.access.checkout.validation.result.state

internal object CvcValidationStateManager: CvcFieldValidationStateManager {

    override val cvcValidationState = FieldValidationState()

    override fun isAllValid(): Boolean {
        return cvcValidationState.validationState
    }
}
