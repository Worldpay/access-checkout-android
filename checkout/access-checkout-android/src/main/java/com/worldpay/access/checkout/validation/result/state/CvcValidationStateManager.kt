package com.worldpay.access.checkout.validation.result.state

internal class CvcValidationStateManager: CvcFieldValidationStateManager {

    override var cvcValidationState = false

    override fun isAllValid(): Boolean {
        return cvcValidationState
    }
}
