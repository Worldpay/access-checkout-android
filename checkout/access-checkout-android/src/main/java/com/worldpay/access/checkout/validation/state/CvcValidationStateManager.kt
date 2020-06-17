package com.worldpay.access.checkout.validation.state

internal class CvcValidationStateManager: CvcFieldValidationStateManager {

    override var cvcValidationState = false

    override fun isAllValid(): Boolean {
        return cvcValidationState
    }
}
