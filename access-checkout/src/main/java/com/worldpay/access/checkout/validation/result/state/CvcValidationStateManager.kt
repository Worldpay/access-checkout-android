package com.worldpay.access.checkout.validation.result.state

import com.worldpay.access.checkout.ui.AccessEditText

internal class CvcValidationStateManager(cvc: AccessEditText) : CvcFieldValidationStateManager {

    override val cvcValidationState = FieldValidationState(cvc.id)

    override fun isAllValid(): Boolean {
        return cvcValidationState.validationState
    }
}
