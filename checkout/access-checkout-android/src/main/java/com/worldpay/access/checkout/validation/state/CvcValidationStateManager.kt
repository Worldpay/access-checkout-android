package com.worldpay.access.checkout.validation.state

internal class CvcValidationStateManager: CvcFieldValidationStateManager {

    override var cvvValidated = false

    override fun isAllValid(): Boolean {
        return cvvValidated
    }
}
